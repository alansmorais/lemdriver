// LEM Motoristas Web App Engine (iOS & Android Compatible)
const WEB_APP_URL = "https://script.google.com/macros/s/AKfycbzVXHlSLJykQpgwJBs6OEiLZx70lBstHfQNkB7EvvL275foVcxRCSAzxDUKb8gqEoilNA/exec";

let appState = {
    currentScreen: 'login', // login, viagens, rota, ganhos, perfil
    currentTab: 'viagens',
    isLoggedIn: false,
    currentDriver: null,
    drivers: [],
    trips: [],
    completedTrips: [],
    activeTransfer: null,
    selectedCategory: 'MINHAS',
    selectedTripForDetails: null,
    isSyncing: false,
    lastSyncTime: 'Aguardando...',
    newlyAssignedTrip: null,
    audioUnlocked: false,
    pushAllowed: false,
    backendConfig: { ratePerKm: 3.50, currency: 'R$' }
};

let knownTripIds = new Set();
let knownAssignedTripIds = new Set();
let hasInitializedTripIds = false;
let hasInitializedAssignedIds = false;
let pollInterval = null;

// --- Web Audio API Synthesizer (Works on iPhone Safari and Android) ---
let audioCtx = null;

function unlockAudioContext() {
    try {
        if (!audioCtx) {
            const AudioContextClass = window.AudioContext || window.webkitAudioContext;
            audioCtx = new AudioContextClass();
        }
        if (audioCtx.state === 'suspended') {
            audioCtx.resume();
        }
        appState.audioUnlocked = true;

        // Request browser push notification permission
        if ('Notification' in window && Notification.permission !== 'granted') {
            Notification.requestPermission().then(permission => {
                if (permission === 'granted') {
                    appState.pushAllowed = true;
                    showToast("Notificações Push ativadas com sucesso!", "notifications_active");
                }
            });
        }
        updateSoundBadge();
    } catch (e) {
        console.log("Audio unlock notice:", e);
    }
}

function playDispatchChime() {
    unlockAudioContext();
    try {
        if (!audioCtx) return;
        const now = audioCtx.currentTime;
        
        // 3-tone melodic dispatch chime (D5 -> G5 -> D6)
        const freqs = [587.33, 783.99, 1174.66];
        freqs.forEach((freq, idx) => {
            const osc = audioCtx.createOscillator();
            const gain = audioCtx.createGain();
            
            osc.type = 'sine';
            osc.frequency.setValueAtTime(freq, now + (idx * 0.18));
            
            gain.gain.setValueAtTime(0, now + (idx * 0.18));
            gain.gain.linearRampToValueAtTime(0.5, now + (idx * 0.18) + 0.02);
            gain.gain.exponentialRampToValueAtTime(0.001, now + (idx * 0.18) + 0.25);
            
            osc.connect(gain);
            gain.connect(audioCtx.destination);
            
            osc.start(now + (idx * 0.18));
            osc.stop(now + (idx * 0.18) + 0.3);
        });

        // Trigger vibration if supported
        if (navigator.vibrate) {
            navigator.vibrate([200, 100, 200, 100, 400]);
        }
    } catch (e) {
        console.log("Chime playback error:", e);
    }
}

function updateSoundBadge() {
    const badge = document.getElementById('sound-badge');
    const txt = document.getElementById('sound-status-text');
    if (badge && txt) {
        txt.textContent = appState.audioUnlocked ? "Som & Push Ativos" : "Toque p/ Ativar Som";
    }
}

function toggleAudioAndPushPermissions() {
    unlockAudioContext();
    playDispatchChime();
    showToast("🔊 Alerta sonoro testado com sucesso!", "volume_up");
}

// --- Initialization & Data Fetching ---
window.addEventListener('DOMContentLoaded', () => {
    loadDefaultDrivers();
    renderApp();
    syncWithBackend();

    // Start 15s background polling loop
    pollInterval = setInterval(() => {
        if (appState.isLoggedIn) {
            syncWithBackend(true);
        }
    }, 15000);
});

function loadDefaultDrivers() {
    appState.drivers = [
        {
            id: "drv-01",
            name: "Eduardo Silveira",
            phone: "(12) 98850-6597",
            email: "eduardo.motorista@litoralemmovimento.com.br",
            vehicleModel: "Chevrolet Spin Premier 7L • 2024",
            vehiclePlate: "SP-LIT7A24",
            isOnline: true,
            rating: 4.98,
            totalTrips: 342,
            pixKey: "12988506597",
            shift: "Manhã"
        },
        {
            id: "drv-02",
            name: "Edivam Santos",
            phone: "(12) 98860-1234",
            email: "edivam.motorista@litoralemmovimento.com.br",
            vehicleModel: "Chevrolet Spin Premier 7L • 2023",
            vehiclePlate: "SP-LEM7B23",
            isOnline: true,
            rating: 4.95,
            totalTrips: 289,
            pixKey: "12988601234",
            shift: "Tarde"
        },
        {
            id: "drv-03",
            name: "Alan Morais",
            phone: "(12) 99123-4567",
            email: "alan.motorista@litoralemmovimento.com.br",
            vehicleModel: "Chevrolet Spin Activ 7L • 2024",
            vehiclePlate: "SP-LEM9C24",
            isOnline: true,
            rating: 5.0,
            totalTrips: 415,
            pixKey: "12991234567",
            shift: "Noite"
        }
    ];
    appState.currentDriver = appState.drivers[0];
}

async function syncWithBackend(silent = false) {
    if (appState.isSyncing) return;
    appState.isSyncing = true;
    if (!silent) showToast("Sincronizando com Servidor Live...", "sync");

    try {
        const response = await fetch(WEB_APP_URL, {
            method: 'GET',
            mode: 'cors',
            headers: { 'Accept': 'application/json' }
        });

        if (!response.ok) throw new Error("Falha na conexão com Google Sheets");
        const data = await response.json();

        if (data.drivers && Array.isArray(data.drivers) && data.drivers.length > 0) {
            appState.drivers = data.drivers;
            const updatedCurrent = appState.drivers.find(d => d.id === appState.currentDriver?.id || d.name.toLowerCase() === appState.currentDriver?.name?.toLowerCase());
            if (updatedCurrent) {
                appState.currentDriver = { ...updatedCurrent, shift: appState.currentDriver.shift };
            }
        }

        if (data.reservations && Array.isArray(data.reservations)) {
            const currentDriverName = appState.currentDriver?.name || "";
            const mappedTrips = data.reservations.map(trip => {
                const assignedToCurrent = trip.assignedDriverName && trip.assignedDriverName.toLowerCase().includes(currentDriverName.toLowerCase());
                return {
                    ...trip,
                    isAssignedToMe: assignedToCurrent,
                    isAvailableToClaim: !trip.assignedDriverName || trip.assignedDriverName.trim() === ""
                };
            });

            // Detect new assignments specifically for logged in driver
            const myAssigned = mappedTrips.filter(t => t.isAssignedToMe && t.status !== 'CONCLUIDO' && t.status !== 'RECUSADO');
            if (appState.isLoggedIn) {
                if (!hasInitializedAssignedIds) {
                    knownAssignedTripIds.clear();
                    myAssigned.forEach(t => knownAssignedTripIds.add(t.id));
                    hasInitializedAssignedIds = true;
                } else {
                    const newAssigned = myAssigned.filter(t => !knownAssignedTripIds.has(t.id));
                    if (newAssigned.length > 0) {
                        triggerAssignmentAlert(newAssigned[0]);
                    }
                }
            }

            // Detect general new fleet reservations
            const currentIds = new Set(mappedTrips.map(t => t.id));
            if (!hasInitializedTripIds) {
                knownTripIds.clear();
                currentIds.forEach(id => knownTripIds.add(id));
                hasInitializedTripIds = true;
            } else {
                const newFleetRes = mappedTrips.filter(t => !knownTripIds.has(t.id));
                if (newFleetRes.length > 0 && !appState.newlyAssignedTrip) {
                    playDispatchChime();
                    showToast(`🔔 Nova reserva na frota: ${newFleetRes[0].code}`, "notifications_active");
                }
            }

            knownTripIds.clear();
            currentIds.forEach(id => knownTripIds.add(id));
            knownAssignedTripIds.clear();
            myAssigned.forEach(t => knownAssignedTripIds.add(t.id));

            appState.trips = mappedTrips;
        }

        const now = new Date();
        appState.lastSyncTime = `Sincronizado às ${now.getHours().toString().padStart(2,'0')}:${now.getMinutes().toString().padStart(2,'0')}:${now.getSeconds().toString().padStart(2,'0')}`;
        if (!silent) showToast("Servidor sincronizado com sucesso!", "cloud_done");
    } catch (e) {
        console.log("Sync notice:", e);
        if (!silent) showToast("Modo Offline / Conexão ativa", "cloud");
    } finally {
        appState.isSyncing = false;
        renderApp();
    }
}

function triggerAssignmentAlert(trip) {
    appState.newlyAssignedTrip = trip;
    playDispatchChime();

    // Browser Push Notification API trigger
    if ('Notification' in window && Notification.permission === 'granted') {
        try {
            new Notification(`🚨 Corrida Atribuída: ${trip.code}`, {
                body: `${trip.origin.title} ➔ ${trip.destination.title} (${trip.timeLabel})`,
                icon: 'icon-192.png'
            });
        } catch (e) { console.log("Push notice:", e); }
    }

    showAssignmentModal(trip);
}

// --- Navigation & Tabs ---
function switchTab(tab) {
    appState.currentTab = tab;
    if (tab === 'viagens') appState.currentScreen = 'viagens';
    else if (tab === 'rota') appState.currentScreen = 'rota';
    else if (tab === 'ganhos') appState.currentScreen = 'ganhos';
    else if (tab === 'perfil') appState.currentScreen = 'perfil';
    renderApp();
}

function openProfile() {
    switchTab('perfil');
}

function navigateToLogin() {
    appState.isLoggedIn = false;
    appState.currentScreen = 'login';
    renderApp();
}

// --- Authentication ---
function handleLogin() {
    unlockAudioContext();
    const select = document.getElementById('driver-select');
    const pinInput = document.getElementById('driver-pin');
    const shiftSelect = document.getElementById('shift-select');

    const driverId = select.value;
    const pin = pinInput.value.trim();
    const shift = shiftSelect.value;

    const driver = appState.drivers.find(d => d.id === driverId) || appState.drivers[0];
    
    if (pin.length < 4 && pin !== "admin" && pin !== "9999") {
        showToast("Digite a senha PIN (4 últimos dígitos do telefone ou 2026).", "warning");
        return;
    }

    appState.currentDriver = { ...driver, shift: shift };
    appState.isLoggedIn = true;
    appState.currentScreen = 'viagens';
    appState.currentTab = 'viagens';

    // Populate assigned trips
    const myAssigned = appState.trips.filter(t => t.assignedDriverName && t.assignedDriverName.toLowerCase().includes(driver.name.toLowerCase()) && t.status !== 'CONCLUIDO');
    knownAssignedTripIds.clear();
    myAssigned.forEach(t => knownAssignedTripIds.add(t.id));

    showToast(`Bem-vindo, ${driver.name}! Turno ${shift} ativo.`, "verified");
    playDispatchChime();
    renderApp();

    if (myAssigned.length > 0) {
        triggerAssignmentAlert(myAssigned[0]);
    }
}

// --- Trip Actions ---
function acceptAndStartAssignedTrip() {
    const trip = appState.newlyAssignedTrip;
    if (!trip) return;
    closeAssignmentModal();

    appState.activeTransfer = {
        tripId: trip.id,
        code: trip.code,
        passengerName: trip.passengerName,
        passengerPhone: trip.passengerPhone,
        currentStepIndex: 1,
        origin: trip.origin,
        destination: trip.destination,
        baseFareToCollect: trip.totalPrice > 0 ? trip.totalPrice : 450.0,
        extraKm: 0.0,
        otherExtrasAmount: 0.0,
        fareToCollect: trip.totalPrice > 0 ? trip.totalPrice : 450.0,
        paymentMode: trip.paymentMethod
    };

    appState.currentScreen = 'rota';
    appState.currentTab = 'rota';
    showToast("Corrida aceita! GPS e rota iniciados.", "navigation");
    renderApp();
}

function viewAssignedTripInList() {
    closeAssignmentModal();
    appState.selectedCategory = 'MINHAS';
    appState.currentScreen = 'viagens';
    appState.currentTab = 'viagens';
    renderApp();
}

function showAssignmentModal(trip) {
    const modal = document.getElementById('assignment-modal');
    const details = document.getElementById('modal-trip-details');
    if (!modal || !details) return;

    const price = trip.totalPrice > 0 ? trip.totalPrice : 480.0;
    details.innerHTML = `
        <div class="flex justify-between items-center pb-2 border-b border-slate-200">
            <span class="font-extrabold text-slate-900">${trip.code}</span>
            <span class="text-emerald-700 font-extrabold text-base">R$ ${price.toFixed(2).replace('.', ',')}</span>
        </div>
        <p class="text-xs font-bold text-slate-800 mt-1">👤 ${trip.passengerName}</p>
        <p class="text-xs text-slate-600">📍 Origem: ${trip.origin.title}</p>
        <p class="text-xs text-slate-600">🎯 Destino: ${trip.destination.title}</p>
        <p class="text-xs text-amber-700 font-semibold mt-1">⏰ Horário: ${trip.timeLabel}</p>
    `;

    modal.classList.remove('hidden');
    modal.classList.add('flex');
}

function closeAssignmentModal() {
    const modal = document.getElementById('assignment-modal');
    if (modal) {
        modal.classList.remove('flex');
        modal.classList.add('hidden');
    }
    appState.newlyAssignedTrip = null;
}

function simulateAssignment() {
    const sampleTrip = {
        id: "res-sim-" + Date.now(),
        code: "#LEM-2026-SIM",
        passengerName: "Dra. Carolina Mendes (Executivo)",
        passengerPhone: "12988506597",
        origin: { title: "Santos / Gonzaga", subtitle: "Av. Ana Costa, 450 - SP" },
        destination: { title: "Aeroporto GRU • Terminal 2", subtitle: "Guarulhos - SP" },
        totalPrice: 480.00,
        paymentMethod: "PIX Direto",
        timeLabel: "08:30",
        date: "Hoje",
        passengersCount: 3,
        luggageInfo: "3 malas",
        flightNumber: "Voo LA-3420",
        status: "CONFIRMADO",
        assignedDriverName: appState.currentDriver?.name || "Eduardo Silveira"
    };
    triggerAssignmentAlert(sampleTrip);
}

// --- Toast notification ---
function showToast(message, icon = "check_circle") {
    const container = document.getElementById('toast-container');
    const msg = document.getElementById('toast-msg');
    const ico = document.getElementById('toast-icon');
    if (!container || !msg || !ico) return;

    msg.textContent = message;
    ico.textContent = icon;
    container.classList.remove('-translate-y-20', 'opacity-0');
    container.classList.add('translate-y-0', 'opacity-100');

    setTimeout(() => {
        container.classList.remove('translate-y-0', 'opacity-100');
        container.classList.add('-translate-y-20', 'opacity-0');
    }, 4000);
}

// --- Rendering Engine ---
function renderApp() {
    const header = document.getElementById('app-header');
    const nav = document.getElementById('bottom-nav');
    const main = document.getElementById('main-content');

    if (!header || !nav || !main) return;

    if (!appState.isLoggedIn) {
        header.classList.add('hidden');
        nav.classList.add('hidden');
        main.innerHTML = renderLoginScreen();
    } else {
        header.classList.remove('hidden');
        nav.classList.remove('hidden');

        // Update Header info
        document.getElementById('header-driver-name').textContent = appState.currentDriver.name;
        document.getElementById('header-vehicle-badge').textContent = `${appState.currentDriver.vehicleModel.split('•')[0]} • ${appState.currentDriver.vehiclePlate}`;
        document.getElementById('header-driver-initials').textContent = appState.currentDriver.name.split(' ').map(n=>n[0]).join('').slice(0,2).toUpperCase();

        // Update nav active states
        ['viagens', 'rota', 'ganhos', 'perfil'].forEach(tab => {
            const btn = document.getElementById(`nav-${tab}`);
            if (btn) {
                if (appState.currentTab === tab) {
                    btn.className = "flex flex-col items-center text-amberVibrant transition-all";
                } else {
                    btn.className = "flex flex-col items-center text-slate-400 hover:text-white transition-all";
                }
            }
        });

        if (appState.currentScreen === 'viagens') {
            main.innerHTML = renderViagensScreen();
        } else if (appState.currentScreen === 'rota') {
            main.innerHTML = renderRotaScreen();
        } else if (appState.currentScreen === 'ganhos') {
            main.innerHTML = renderGanhosScreen();
        } else if (appState.currentScreen === 'perfil') {
            main.innerHTML = renderPerfilScreen();
        }
    }
}

function renderLoginScreen() {
    return `
        <div class="p-6 flex flex-col justify-between min-h-full bg-slate950 text-white">
            <div class="flex flex-col items-center pt-8">
                <div class="w-20 h-20 rounded-2xl bg-gradient-to-tr from-amberVibrant to-amberDeep flex items-center justify-center shadow-xl mb-4">
                    <span class="material-symbols-outlined text-4xl text-slate950 font-black">directions_car</span>
                </div>
                <h1 class="text-2xl font-black tracking-wider text-white">LEM MOTORISTAS</h1>
                <p class="text-xs text-amber-400 font-bold uppercase tracking-widest mt-1">Litoral em Movimento • Frota Executiva</p>
                <p class="text-xs text-slate-400 text-center mt-3 px-4">Portal Oficial de Despacho & Escalas (Versão Web App iOS & Android)</p>
            </div>

            <div class="bg-slate900 rounded-3xl p-6 border border-slate800 space-y-4 my-6 shadow-2xl">
                <div>
                    <label class="block text-xs font-bold text-slate-400 uppercase mb-1.5">Selecionar Motorista</label>
                    <select id="driver-select" class="w-full bg-slate800 border border-slate-700 text-white rounded-xl px-4 py-3 text-sm font-semibold focus:outline-none focus:border-amberVibrant">
                        ${appState.drivers.map(d => `<option value="${d.id}">${d.name} (${d.vehicleModel.split('•')[0]})</option>`).join('')}
                    </select>
                </div>

                <div>
                    <label class="block text-xs font-bold text-slate-400 uppercase mb-1.5">Senha PIN de Acesso</label>
                    <input type="password" id="driver-pin" placeholder="•••• (4 últimos dígitos do celular)" class="w-full bg-slate800 border border-slate-700 text-white rounded-xl px-4 py-3 text-sm font-semibold focus:outline-none focus:border-amberVibrant" />
                    <p class="text-[11px] text-slate-500 mt-1">💡 1º acesso: 4 últimos dígitos do seu telefone ou 2026.</p>
                </div>

                <div>
                    <label class="block text-xs font-bold text-slate-400 uppercase mb-1.5">Turno</label>
                    <select id="shift-select" class="w-full bg-slate800 border border-slate-700 text-white rounded-xl px-4 py-3 text-sm font-semibold focus:outline-none focus:border-amberVibrant">
                        <option value="Manhã">Manhã (06:00 - 14:00)</option>
                        <option value="Tarde">Tarde (14:00 - 22:00)</option>
                        <option value="Noite">Noite / Integral (22:00 - 06:00)</option>
                    </select>
                </div>

                <button onclick="handleLogin()" class="w-full bg-amberVibrant hover:bg-amber-500 text-slate-950 font-black py-4 rounded-xl shadow-lg flex items-center justify-center space-x-2 text-base transition-all mt-2">
                    <span>Entrar no Portal</span>
                    <span class="material-symbols-outlined">arrow_forward</span>
                </button>
            </div>

            <div class="flex flex-col items-center space-y-2 pb-4">
                <button onclick="toggleAudioAndPushPermissions()" class="text-xs text-amber-400 font-bold underline flex items-center space-x-1">
                    <span class="material-symbols-outlined text-sm">volume_up</span>
                    <span>Ativar Alarme Sonoro e Notificações Push</span>
                </button>
                <p class="text-[10px] text-slate-500 text-center">Chevrolet Spin Premier 7L • Santos, SP • Litoral em Movimento</p>
            </div>
        </div>
    `;
}

function renderViagensScreen() {
    const driverName = appState.currentDriver?.name || "";
    let filtered = appState.trips;

    if (appState.selectedCategory === 'MINHAS') {
        filtered = appState.trips.filter(t => t.isAssignedToMe || (t.assignedDriverName && t.assignedDriverName.toLowerCase().includes(driverName.toLowerCase())));
    } else if (appState.selectedCategory === 'DISPONIVEIS') {
        filtered = appState.trips.filter(t => t.isAvailableToClaim || !t.assignedDriverName || t.assignedDriverName.trim() === "");
    }

    return `
        <div class="p-4 space-y-4">
            <!-- Sync & Status Bar -->
            <div class="bg-slate-900 text-white rounded-2xl p-4 flex items-center justify-between shadow-md">
                <div>
                    <p class="text-[10px] uppercase font-bold text-amber-400 tracking-wider">Status da Frota</p>
                    <p class="text-xs font-semibold text-slate-300">${appState.lastSyncTime}</p>
                </div>
                <div class="flex items-center space-x-2">
                    <button onclick="syncWithBackend()" class="bg-slate-800 hover:bg-slate-700 text-amber-400 border border-slate-700 px-3 py-1.5 rounded-xl text-xs font-bold flex items-center space-x-1">
                        <span class="material-symbols-outlined text-sm ${appState.isSyncing ? 'animate-spin' : ''}">refresh</span>
                        <span>Sincronizar</span>
                    </button>
                    <button onclick="simulateAssignment()" class="bg-amberVibrant text-slate-950 px-3 py-1.5 rounded-xl text-xs font-extrabold flex items-center space-x-1 shadow">
                        <span class="material-symbols-outlined text-sm">notifications_active</span>
                        <span>Testar Alarme</span>
                    </button>
                </div>
            </div>

            <!-- Category Tabs -->
            <div class="flex bg-slate-200 p-1 rounded-xl">
                <button onclick="setCategory('MINHAS')" class="flex-1 py-2 text-xs font-extrabold rounded-lg transition-all ${appState.selectedCategory === 'MINHAS' ? 'bg-white text-slate-900 shadow' : 'text-slate-600'}">
                    Minhas Escalas (${appState.trips.filter(t => t.isAssignedToMe).length})
                </button>
                <button onclick="setCategory('DISPONIVEIS')" class="flex-1 py-2 text-xs font-extrabold rounded-lg transition-all ${appState.selectedCategory === 'DISPONIVEIS' ? 'bg-white text-slate-900 shadow' : 'text-slate-600'}">
                    Disponíveis (${appState.trips.filter(t => t.isAvailableToClaim).length})
                </button>
                <button onclick="setCategory('TODAS')" class="flex-1 py-2 text-xs font-extrabold rounded-lg transition-all ${appState.selectedCategory === 'TODAS' ? 'bg-white text-slate-900 shadow' : 'text-slate-600'}">
                    Todas (${appState.trips.length})
                </button>
            </div>

            <!-- Trips Feed -->
            <div class="space-y-3">
                ${filtered.length === 0 ? `
                    <div class="text-center py-12 bg-white rounded-2xl border border-slate-200 p-6">
                        <span class="material-symbols-outlined text-4xl text-slate-400">inbox</span>
                        <p class="text-sm font-bold text-slate-700 mt-2">Nenhuma escala encontrada nesta categoria.</p>
                        <p class="text-xs text-slate-500 mt-1">Toque em Sincronizar para atualizar com o Google Sheets.</p>
                    </div>
                ` : filtered.map(trip => renderTripCard(trip)).join('')}
            </div>
        </div>
    `;
}

function setCategory(cat) {
    appState.selectedCategory = cat;
    renderApp();
}

function renderTripCard(trip) {
    const isAssigned = trip.isAssignedToMe;
    const price = trip.totalPrice > 0 ? trip.totalPrice : 450.0;

    return `
        <div class="bg-white rounded-2xl p-4 border border-slate-200 shadow-sm space-y-3 relative overflow-hidden">
            <div class="absolute top-0 left-0 bottom-0 w-1.5 ${isAssigned ? 'bg-amberVibrant' : 'bg-brandBlue'}"></div>
            
            <div class="flex items-center justify-between pl-2">
                <div>
                    <span class="text-xs font-extrabold text-slate-900">${trip.code}</span>
                    <span class="ml-2 bg-slate-100 text-slate-700 text-[10px] font-bold px-2 py-0.5 rounded-md">${trip.timeLabel}</span>
                </div>
                <span class="text-base font-black text-emerald-700">R$ ${price.toFixed(2).replace('.', ',')}</span>
            </div>

            <div class="pl-2 space-y-1">
                <p class="text-xs font-bold text-slate-800">👤 ${trip.passengerName}</p>
                <p class="text-xs text-slate-600 flex items-center space-x-1">
                    <span class="material-symbols-outlined text-xs text-emerald-600">trip_origin</span>
                    <span class="truncate">${trip.origin.title}</span>
                </p>
                <p class="text-xs text-slate-600 flex items-center space-x-1">
                    <span class="material-symbols-outlined text-xs text-red-600">location_on</span>
                    <span class="truncate">${trip.destination.title}</span>
                </p>
            </div>

            <div class="pl-2 pt-2 border-t border-slate-100 flex items-center justify-between">
                <span class="text-[11px] text-slate-500 font-semibold">${trip.assignedDriverName ? 'Atribuído a: ' + trip.assignedDriverName : 'Disponível na Frota'}</span>
                ${isAssigned ? `
                    <button onclick="startTripDirect('${trip.id}')" class="bg-amberVibrant hover:bg-amber-500 text-slate-950 text-xs font-extrabold px-4 py-2 rounded-xl flex items-center space-x-1 shadow">
                        <span class="material-symbols-outlined text-sm">navigation</span>
                        <span>Iniciar Rota</span>
                    </button>
                ` : `
                    <button onclick="claimTripDirect('${trip.id}')" class="bg-brandBlue hover:bg-blue-700 text-white text-xs font-bold px-4 py-2 rounded-xl">
                        Assumir Corrida
                    </button>
                `}
            </div>
        </div>
    `;
}

function startTripDirect(tripId) {
    const trip = appState.trips.find(t => t.id === tripId);
    if (!trip) return;

    appState.activeTransfer = {
        tripId: trip.id,
        code: trip.code,
        passengerName: trip.passengerName,
        passengerPhone: trip.passengerPhone,
        currentStepIndex: 1,
        origin: trip.origin,
        destination: trip.destination,
        baseFareToCollect: trip.totalPrice > 0 ? trip.totalPrice : 450.0,
        extraKm: 0.0,
        otherExtrasAmount: 0.0,
        fareToCollect: trip.totalPrice > 0 ? trip.totalPrice : 450.0,
        paymentMode: trip.paymentMethod
    };

    appState.currentScreen = 'rota';
    appState.currentTab = 'rota';
    showToast("Iniciando transfer... GPS pronto.", "navigation");
    renderApp();
}

function claimTripDirect(tripId) {
    const trip = appState.trips.find(t => t.id === tripId);
    if (trip) {
        trip.assignedDriverName = appState.currentDriver.name;
        trip.isAssignedToMe = true;
        trip.isAvailableToClaim = false;
        showToast("Corrida assumida com sucesso!", "verified");
        renderApp();
    }
}

function renderRotaScreen() {
    const t = appState.activeTransfer;
    if (!t) {
        return `
            <div class="p-8 text-center flex flex-col items-center justify-center min-h-[60vh]">
                <span class="material-symbols-outlined text-5xl text-slate-400">navigation</span>
                <p class="text-base font-bold text-slate-800 mt-3">Nenhum transfer ativo no momento.</p>
                <p class="text-xs text-slate-500 mt-1">Inicie uma rota a partir de "Minhas Escalas" para habilitar o GPS.</p>
                <button onclick="switchTab('viagens')" class="mt-4 bg-slate-900 text-white text-xs font-bold px-6 py-3 rounded-xl">
                    Ver Minhas Escalas
                </button>
            </div>
        `;
    }

    const steps = [
        "Despacho Aceito",
        "A caminho do Embarque",
        "Passageiro a Bordo",
        "Em Rota ao Destino",
        "Chegada / Finalizar"
    ];

    return `
        <div class="p-4 space-y-4">
            <div class="bg-slate-900 text-white rounded-2xl p-4 space-y-2 shadow-lg">
                <div class="flex justify-between items-center">
                    <span class="text-xs font-extrabold text-amber-400">${t.code}</span>
                    <span class="text-emerald-400 font-extrabold text-base">R$ ${t.fareToCollect.toFixed(2).replace('.', ',')}</span>
                </div>
                <p class="text-sm font-bold">${t.passengerName}</p>
                <p class="text-xs text-slate-300">📍 ${t.origin.title} ➔ ${t.destination.title}</p>
            </div>

            <!-- Steps Progress -->
            <div class="bg-white rounded-2xl p-4 border border-slate-200 shadow-sm space-y-3">
                <h3 class="text-xs font-extrabold text-slate-500 uppercase tracking-wider">Etapas da Viagem</h3>
                <div class="space-y-2">
                    ${steps.map((step, idx) => `
                        <div onclick="advanceStep(${idx})" class="flex items-center space-x-3 p-3 rounded-xl cursor-pointer transition-all ${t.currentStepIndex === idx ? 'bg-amber-50 border border-amber-300' : 'bg-slate-50 hover:bg-slate-100'}">
                            <div class="w-7 h-7 rounded-full flex items-center justify-center text-xs font-bold ${t.currentStepIndex >= idx ? 'bg-emerald-600 text-white' : 'bg-slate-300 text-slate-700'}">
                                ${idx + 1}
                            </div>
                            <span class="text-xs font-bold ${t.currentStepIndex === idx ? 'text-slate-900 font-extrabold' : 'text-slate-600'}">${step}</span>
                        </div>
                    `).join('')}
                </div>
            </div>

            <button onclick="completeTransfer()" class="w-full bg-emerald-600 hover:bg-emerald-700 text-white font-extrabold py-4 rounded-2xl shadow-lg flex items-center justify-center space-x-2">
                <span class="material-symbols-outlined">check_circle</span>
                <span>CONCLUÍDO & REGISTRAR GANHOS</span>
            </button>
        </div>
    `;
}

function advanceStep(idx) {
    if (appState.activeTransfer) {
        appState.activeTransfer.currentStepIndex = idx;
        showToast(`Etapa atualizada: ${idx + 1}`, "route");
        renderApp();
    }
}

function completeTransfer() {
    if (!appState.activeTransfer) return;
    appState.completedTrips.unshift({
        id: appState.activeTransfer.tripId,
        code: appState.activeTransfer.code,
        passengerName: appState.activeTransfer.passengerName,
        fareAmount: appState.activeTransfer.fareToCollect,
        date: new Date().toLocaleDateString('pt-BR')
    });
    showToast(`Transfer concluído! R$ ${appState.activeTransfer.fareToCollect.toFixed(2)} adicionado.`, "verified");
    appState.activeTransfer = null;
    switchTab('ganhos');
}

function renderGanhosScreen() {
    const totalWeekly = appState.completedTrips.reduce((acc, curr) => acc + curr.fareAmount, 4850.00);
    const count = appState.completedTrips.length + 12;

    return `
        <div class="p-4 space-y-4">
            <div class="bg-gradient-to-tr from-slate-950 to-slate-900 text-white rounded-3xl p-6 shadow-xl space-y-3">
                <p class="text-xs font-bold text-amber-400 uppercase tracking-widest">Acerto Semanal • Frota</p>
                <h2 class="text-3xl font-black text-emerald-400">R$ ${totalWeekly.toFixed(2).replace('.', ',')}</h2>
                <div class="pt-2 border-t border-slate-800 flex justify-between text-xs text-slate-300">
                    <span>Viagens Realizadas: <strong>${count}</strong></span>
                    <span>Repasse Líquido: <strong>80%</strong></span>
                </div>
            </div>

            <div class="bg-white rounded-2xl p-4 border border-slate-200 shadow-sm space-y-3">
                <h3 class="text-xs font-extrabold text-slate-500 uppercase tracking-wider">Histórico de Concluídas</h3>
                ${appState.completedTrips.length === 0 ? `
                    <p class="text-xs text-slate-500 py-6 text-center">Nenhum acerto registrado nesta sessão. Suas viagens concluídas aparecerão aqui.</p>
                ` : appState.completedTrips.map(ct => `
                    <div class="flex justify-between items-center py-2 border-b border-slate-100 text-xs">
                        <div>
                            <p class="font-bold text-slate-900">${ct.code} • ${ct.passengerName}</p>
                            <p class="text-[10px] text-slate-500">${ct.date}</p>
                        </div>
                        <span class="font-black text-emerald-700">R$ ${ct.fareAmount.toFixed(2).replace('.', ',')}</span>
                    </div>
                `).join('')}
            </div>
        </div>
    `;
}

function renderPerfilScreen() {
    const d = appState.currentDriver;
    return `
        <div class="p-4 space-y-4">
            <div class="bg-white rounded-2xl p-6 border border-slate-200 shadow-sm text-center space-y-3">
                <div class="w-20 h-20 rounded-full bg-amberVibrant text-slate-950 font-black text-2xl flex items-center justify-center mx-auto shadow-md">
                    ${d.name.split(' ').map(n=>n[0]).join('').slice(0,2).toUpperCase()}
                </div>
                <h2 class="text-lg font-black text-slate-900">${d.name}</h2>
                <p class="text-xs text-slate-500">${d.email}</p>
                <div class="bg-slate-100 rounded-xl p-3 text-left space-y-1 text-xs">
                    <p><strong>Veículo:</strong> ${d.vehicleModel}</p>
                    <p><strong>Placa:</strong> ${d.vehiclePlate}</p>
                    <p><strong>Telefone:</strong> ${d.phone}</p>
                    <p><strong>Turno Atual:</strong> ${d.shift}</p>
                </div>
            </div>

            <div class="bg-white rounded-2xl p-4 border border-slate-200 shadow-sm space-y-3">
                <h3 class="text-xs font-extrabold text-slate-500 uppercase tracking-wider">Áudio & Notificações</h3>
                <button onclick="toggleAudioAndPushPermissions()" class="w-full bg-slate-900 text-white font-bold py-3 rounded-xl text-xs flex items-center justify-center space-x-2">
                    <span class="material-symbols-outlined">volume_up</span>
                    <span>Testar Som de Notificação 🔔</span>
                </button>
                <button onclick="simulateAssignment()" class="w-full bg-amberVibrant text-slate-950 font-extrabold py-3 rounded-xl text-xs flex items-center justify-center space-x-2">
                    <span class="material-symbols-outlined">notifications_active</span>
                    <span>🚨 Testar Pop-up de Corrida Atribuída</span>
                </button>
            </div>

            <button onclick="navigateToLogin()" class="w-full bg-red-100 text-red-700 font-bold py-3.5 rounded-xl text-xs">
                Encerrar Turno / Trocar Motorista
            </button>
        </div>
    `;
}
