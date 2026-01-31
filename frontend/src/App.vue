<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'

const data = ref(null)
const history = ref([])
const connected = ref(false)
const maxHistory = 50

let ws = null

function connect() {
  ws = new WebSocket('ws://localhost:8080/ws/telemetry')
  ws.onopen = () => connected.value = true
  ws.onclose = () => { connected.value = false; setTimeout(connect, 2000) }
  ws.onmessage = (e) => {
    data.value = JSON.parse(e.data)
    history.value.push({ 
      time: new Date().toLocaleTimeString(), 
      speed: data.value.speed, 
      rpm: data.value.rpm 
    })
    if (history.value.length > maxHistory) history.value.shift()
  }
}

async function control(action, params = {}) {
  const query = new URLSearchParams(params).toString()
  await fetch(`http://localhost:8080/api/control/${action}?${query}`, { method: 'POST' })
}

const speedPercent = computed(() => data.value ? (data.value.speed / 280) * 100 : 0)
const rpmPercent = computed(() => data.value ? ((data.value.rpm - 800) / 7200) * 100 : 0)
const tempColor = computed(() => {
  if (!data.value) return 'bg-green-500'
  if (data.value.engineTemp > 105) return 'bg-red-500'
  if (data.value.engineTemp > 95) return 'bg-yellow-500'
  return 'bg-green-500'
})

onMounted(connect)
onUnmounted(() => { if (ws) ws.close() })
</script>

<template>
  <div class="min-h-screen bg-gray-900 text-white p-6">
    <header class="flex items-center justify-between mb-6">
      <div>
        <h1 class="text-2xl font-bold">Vehicle Telemetry Dashboard</h1>
        <p class="text-gray-400 text-sm">Real-time sensor data</p>
      </div>
      <span :class="connected ? 'text-green-400' : 'text-red-400'" class="flex items-center gap-2">
        <span class="w-2 h-2 rounded-full" :class="connected ? 'bg-green-400' : 'bg-red-400'"></span>
        {{ connected ? 'Connected' : 'Disconnected' }}
      </span>
    </header>

    <div v-if="data?.warnings?.length" class="mb-4 p-3 bg-red-900/50 border border-red-500 rounded-lg">
      ⚠️ Warnings: {{ data.warnings.join(', ') }}
    </div>

    <div class="grid grid-cols-2 lg:grid-cols-4 gap-4 mb-6">
      <div class="bg-gray-800 rounded-xl p-4">
        <p class="text-gray-400 text-sm mb-2">Speed</p>
        <div class="h-3 bg-gray-700 rounded-full mb-2">
          <div class="h-full bg-blue-500 rounded-full transition-all" :style="{ width: speedPercent + '%' }"></div>
        </div>
        <p class="text-2xl font-bold">{{ data?.speed || 0 }} <span class="text-sm text-gray-400">km/h</span></p>
      </div>

      <div class="bg-gray-800 rounded-xl p-4">
        <p class="text-gray-400 text-sm mb-2">RPM</p>
        <div class="h-3 bg-gray-700 rounded-full mb-2">
          <div class="h-full bg-green-500 rounded-full transition-all" :class="{ 'bg-red-500': data?.rpm > 7000 }" :style="{ width: rpmPercent + '%' }"></div>
        </div>
        <p class="text-2xl font-bold">{{ data?.rpm || 0 }} <span class="text-sm text-gray-400">rpm</span></p>
      </div>

      <div class="bg-gray-800 rounded-xl p-4">
        <p class="text-gray-400 text-sm mb-2">Engine Temp</p>
        <div class="h-3 bg-gray-700 rounded-full mb-2">
          <div class="h-full rounded-full transition-all" :class="tempColor" :style="{ width: ((data?.engineTemp - 70) / 50 * 100) + '%' }"></div>
        </div>
        <p class="text-2xl font-bold">{{ data?.engineTemp || 0 }} <span class="text-sm text-gray-400">°C</span></p>
      </div>

      <div class="bg-gray-800 rounded-xl p-4">
        <p class="text-gray-400 text-sm mb-2">Gear</p>
        <p class="text-4xl font-bold text-purple-400">{{ data?.gear || 'P' }}</p>
      </div>
    </div>

    <div class="grid grid-cols-2 lg:grid-cols-4 gap-4 mb-6">
      <div class="bg-gray-800 rounded-xl p-4">
        <p class="text-gray-400 text-sm mb-2">Battery</p>
        <div class="h-3 bg-gray-700 rounded-full mb-2">
          <div class="h-full bg-green-500 rounded-full" :class="{ 'bg-yellow-500': data?.battery < 30 }" :style="{ width: (data?.battery || 0) + '%' }"></div>
        </div>
        <p class="text-xl font-bold">{{ data?.battery || 0 }}%</p>
      </div>

      <div class="bg-gray-800 rounded-xl p-4">
        <p class="text-gray-400 text-sm mb-2">Fuel</p>
        <div class="h-3 bg-gray-700 rounded-full mb-2">
          <div class="h-full bg-blue-500 rounded-full" :class="{ 'bg-red-500': data?.fuel < 15 }" :style="{ width: (data?.fuel || 0) + '%' }"></div>
        </div>
        <p class="text-xl font-bold">{{ data?.fuel || 0 }}%</p>
      </div>

      <div class="bg-gray-800 rounded-xl p-4">
        <p class="text-gray-400 text-sm mb-2">Throttle</p>
        <div class="h-3 bg-gray-700 rounded-full mb-2">
          <div class="h-full bg-orange-500 rounded-full" :style="{ width: (data?.throttle || 0) + '%' }"></div>
        </div>
        <p class="text-xl font-bold">{{ data?.throttle || 0 }}%</p>
      </div>

      <div class="bg-gray-800 rounded-xl p-4">
        <p class="text-gray-400 text-sm mb-2">Brake</p>
        <div class="h-3 bg-gray-700 rounded-full mb-2">
          <div class="h-full bg-red-500 rounded-full" :style="{ width: (data?.brake || 0) + '%' }"></div>
        </div>
        <p class="text-xl font-bold">{{ data?.brake || 0 }}%</p>
      </div>
    </div>

    <div class="flex gap-4 mb-6">
      <button @click="control('accelerate', { intensity: 0.8 })" class="flex-1 py-3 bg-green-600 hover:bg-green-700 rounded-lg font-medium">Accelerate</button>
      <button @click="control('brake', { intensity: 0.8 })" class="flex-1 py-3 bg-red-600 hover:bg-red-700 rounded-lg font-medium">Brake</button>
      <button @click="control('reset')" class="px-6 py-3 bg-gray-700 hover:bg-gray-600 rounded-lg font-medium">Reset</button>
    </div>

    <div class="bg-gray-800 rounded-xl p-4">
      <h3 class="text-lg font-medium mb-4">History</h3>
      <div class="h-32 flex items-end gap-1">
        <div v-for="(h, i) in history" :key="i" class="flex-1 bg-blue-500/50 rounded-t" :style="{ height: (h.speed / 280 * 100) + '%' }"></div>
      </div>
      <p class="text-gray-400 text-sm mt-2">Speed over time</p>
    </div>

    <footer class="mt-8 text-center text-gray-500 text-sm">
      Vehicle Telemetry Dashboard v1.0.0
    </footer>
  </div>
</template>
