<script>
	const { ipcRenderer } = require('electron');

	let personalStats = {
		apm: 0,
		gcd: 1.5,
		dps: 0,
		hps: 0,
		dtps: 0,
		htps: 0,
		dabps: 0,
		tps: 0,
		delay: 0,
	};

	ipcRenderer.on('update_personal_stats', function (event, arg) {
		personalStats = arg;
	});

	function close() {
		ipcRenderer.invoke('close_personal_overlay');
	}
</script>

<div class="p-1 overlay-title-bar d-flex flex-justify-between flex-items-center">
	<div class="f6">Personal</div>
	<div>
		<button class="close-button overlay-title-bar-drag mr-2" type="button" om:click={close} style="z-index: 10;">
			<svg
				xmlns="http://www.w3.org/2000/svg"
				style="width: 16px; height 16px; transform: rotate(41deg)"
				fill="none"
				viewBox="0 0 24 24"
				stroke="currentColor">
				<path
					stroke-linecap="round"
					stroke-linejoin="round"
					stroke-width="2"
					d="M4 8V4m0 0h4M4 4l5 5m11-1V4m0 0h-4m4 0l-5 5M4 16v4m0 0h4m-4 0l5-5m11 5l-5-5m5 5v-4m0 4h-4" />
			</svg>
		</button>
		<button class="close-button" type="button" on:click={close} style="z-index: 10;">
			<svg xmlns="http://www.w3.org/2000/svg" style="width: 16px; height 16px;" viewBox="0 0 20 20" fill="currentColor">
				<path
					fill-rule="evenodd"
					d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z"
					clip-rule="evenodd" />
			</svg>
		</button>
	</div>
</div>

<ul class="p-1 f6">
	<!-- APM -->
	<li class="my-1 d-flex flex-justify-between">
		<span class="f6">APM:</span>
		<span class="text-bold">
			{personalStats.apm.toLocaleString(undefined, {
				minimumFractionDigits: 2,
				maximumFractionDigits: 2,
			})}
		</span>
	</li>

	<!-- GCD -->
	<li class="my-1 d-flex flex-justify-between">
		<span class="f6">GCD:</span>
		<span class="text-bold" style="color: #b392f0">
			{personalStats.gcd.toLocaleString(undefined, {
				minimumFractionDigits: 2,
				maximumFractionDigits: 2,
			})}
		</span>
	</li>

	<!-- DPS -->
	<li class="my-1 d-flex flex-justify-between">
		<span class="f6">DPS:</span>
		<span class="text-bold" style="color: #f97583;">
			{personalStats.dps.toLocaleString(undefined, {
				minimumFractionDigits: 2,
				maximumFractionDigits: 2,
			})}
		</span>
	</li>

	<!-- HPS -->
	<li class="my-1 d-flex flex-justify-between">
		<span class="f6">HPS:</span>
		<span class="text-bold" style="color: #85e89d;">
			{personalStats.hps.toLocaleString(undefined, {
				minimumFractionDigits: 2,
				maximumFractionDigits: 2,
			})}
		</span>
	</li>

	<!-- TPS -->
	<li class="my-1 d-flex flex-justify-between">
		<span class="f6">TPS:</span>
		<span class="text-bold" style="color: #ffea7f;">
			{personalStats.tps.toLocaleString(undefined, {
				minimumFractionDigits: 2,
				maximumFractionDigits: 2,
			})}
		</span>
	</li>

	<!-- DTPS -->
	<li class="my-1 d-flex flex-justify-between mt-2">
		<span class="f6">DTPS:</span>
		<span class="text-bold" style="color: #ff7b72;">
			{personalStats.dtps.toLocaleString(undefined, {
				minimumFractionDigits: 2,
				maximumFractionDigits: 2,
			})}
		</span>
	</li>

	<!-- HTPS -->
	<li class="my-1 d-flex flex-justify-between">
		<span class="f6">HTPS:</span>
		<span class="text-bold" style="color: #3fb950;">
			{personalStats.htps.toLocaleString(undefined, {
				minimumFractionDigits: 2,
				maximumFractionDigits: 2,
			})}
		</span>
	</li>

	<!-- DAPS -->
	<li class="my-1 d-flex flex-justify-between">
		<span class="f6">DAPS:</span>
		<span class="text-bold" style="color: #58a6ff;">
			{personalStats.dabps.toLocaleString(undefined, {
				minimumFractionDigits: 2,
				maximumFractionDigits: 2,
			})}
		</span>
	</li>

	<!-- DELAY -->
	<li class="my-1 d-flex flex-justify-between mt-2">
		<span class="f6">Delay:</span>
		<span class="text-bold">
			{personalStats.delay.toLocaleString(undefined, {
				minimumFractionDigits: 2,
				maximumFractionDigits: 2,
			})}s ago
		</span>
	</li>
</ul>
