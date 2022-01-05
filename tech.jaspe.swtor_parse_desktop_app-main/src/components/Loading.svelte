<script>
	export let value = 0;
</script>

<div class="delay d-flex flex-justify-center overlay">
	<div class="flex-self-center d-flex flex-items-center flex-column" style="opacity: 100%;">
		<div class="d-flex flex-column p-4 color-shadow-medium rounded" style="width: 300px; background: #0d1117;">
			<p>
				<span>Loading {value.toFixed(2)}%</span><span class="AnimatedEllipsis" />
			</p>

			<span class="mt-2 Progress Progress--large">
				<span class="Progress-item color-bg-success-inverse" style="width: {value}%;" />
			</span>
		</div>
	</div>
</div>
