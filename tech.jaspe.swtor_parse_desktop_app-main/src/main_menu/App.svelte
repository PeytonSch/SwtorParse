<script>
	import { tick } from 'svelte';
	import Loading from '../components/Loading.svelte';
	import Settings from '../components/Settings.svelte';

	const moment = require('moment-timezone');
	const { median } = require('mathjs');
	const _ = require('lodash');
	const fs = require('fs');
	const os = require('os');
	const fsPromises = fs.promises;
	const { ipcRenderer } = require('electron');
	const parse = new Worker('parseFile.js');

	const MOMENT_FORMAT = 'YYYY-MM-DD HH:mm:ss.SS';
	moment.defaultFormat = 'YYYY-MM-DD HH:mm:ss.SS';

	let loading = false;
	// let path = 'D:/Work/tech.jaspe.swtor_parse_desktop_app/test';
	let path = os.userInfo().homedir.replace(/\\/g, '/') + '/Documents/Star Wars - The Old Republic/CombatLogs';
	let files = [];
	let selectedFile = '';
	let selectedFilePrevious = '';
	let realTimeParse = false;
	let loadDPSTimeLineReload;
	let loadHPSTimeLineReload;
	let loadDTPSTimeLineReload;
	let loadTPSTimeLineReload;
	let loadHitsGraphicReload;
	let loadGCDPerAbilityReload;
	let loadGCD_DPSTimelineReload;
	let loadGCD_HPSTimelineReload;
	let combat;
	let currentCombatHasChanges = null;
	let selectedMenu;
	let logs = {};
	let percentage = 0;
	let noFileSelected = false;
	let selectedTab = 'damage';

	async function onSelectedMenuItem(item) {
		selectedMenu = item;
		await tick();

		if (selectedMenu === 'analyze') {
			await loadStats(combat);
		}
	}

	function resetDashboard() {
		ipcRenderer.send('update_personal_stats', {
			apm: 0,
			gcd: 1.5,
			dps: 0,
			hps: 0,
			dtps: 0,
			htps: 0,
			dabps: 0,
			tps: 0,
			delay: 0,
		});

		selectedTab = 'damage';
		combat = undefined;
		disposeCharts();
	}

	async function selectCombat() {
		await tick();

		if (realTimeParse) return;
		if (selectedFile === '') return;
		if (selectedFilePrevious === selectedFile) return;
		noFileSelected = true;
		selectedTab = 'damage';

		disposeCharts();
		combat = undefined;
		await tick();

		selectedFilePrevious = selectedFile;
		percentage = 0;
		loading = true;

		if (logs[selectedFile] === null && !Array.isArray(logs[selectedFile])) {
			parse.postMessage(
				JSON.stringify({
					type: 'parse_logs',
					path: path,
					files: [selectedFile],
				})
			);
		} else {
			await loadStats(logs[selectedFile][0]);
			loading = false;
		}
	}

	async function loadStats(c) {
		if (typeof c === 'undefined') return;

		selectedTab = 'damage';
		combat = c;
		await tick();

		ipcRenderer.send('update_personal_stats', {
			apm: combat.apm,
			gcd: combat.gcdMedian,
			dps: combat.dps,
			hps: combat.hps,
			dtps: combat.dtps,
			htps: combat.htps,
			dabps: combat.dabps,
			tps: combat.tps,
			delay: moment().diff(moment(combat.logs[combat.logs.length - 1].timestamp, MOMENT_FORMAT), 'seconds', true),
		});

		if (selectedMenu !== 'analyze') return;
		if (realTimeParse) return;

		loadDPSTimeLineReload = typeof loadDPSTimeLineReload === 'undefined' ? loadDPSTimeLine() : loadDPSTimeLineReload();
		loadHPSTimeLineReload = typeof loadHPSTimeLineReload === 'undefined' ? loadHPSTimeLine() : loadHPSTimeLineReload();
		loadDTPSTimeLineReload =
			typeof loadDTPSTimeLineReload === 'undefined' ? loadDTPSTimeLine() : loadDTPSTimeLineReload();
		loadTPSTimeLineReload = typeof loadTPSTimeLineReload === 'undefined' ? loadTPSTimeLine() : loadTPSTimeLineReload();
		loadHitsGraphicReload = typeof loadHitsGraphicReload === 'undefined' ? loadHitsGraphic() : loadHitsGraphicReload();
		loadGCDPerAbilityReload =
			typeof loadGCDPerAbilityReload === 'undefined' ? loadGCDPerAbility() : loadGCDPerAbilityReload();
		loadGCD_DPSTimelineReload =
			typeof loadGCD_DPSTimelineReload === 'undefined' ? loadGCD_DPSTimeline() : loadGCD_DPSTimelineReload();
		loadGCD_HPSTimelineReload =
			typeof loadGCD_HPSTimelineReload === 'undefined' ? loadGCD_HPSTimeline() : loadGCD_HPSTimelineReload();
	}

	function disposeCharts() {
		if (typeof loadDPSTimeLineReload !== 'undefined') {
			loadDPSTimeLineReload(true);
			loadDPSTimeLineReload = undefined;
		}

		if (typeof loadHPSTimeLineReload !== 'undefined') {
			loadHPSTimeLineReload(true);
			loadHPSTimeLineReload = undefined;
		}

		if (typeof loadDTPSTimeLineReload !== 'undefined') {
			loadDTPSTimeLineReload(true);
			loadDTPSTimeLineReload = undefined;
		}

		if (typeof loadTPSTimeLineReload !== 'undefined') {
			loadTPSTimeLineReload(true);
			loadTPSTimeLineReload = undefined;
		}

		if (typeof loadHitsGraphicReload !== 'undefined') {
			loadHitsGraphicReload(true);
			loadHitsGraphicReload = undefined;
		}

		if (typeof loadGCDPerAbilityReload !== 'undefined') {
			loadGCDPerAbilityReload(true);
			loadGCDPerAbilityReload = undefined;
		}

		if (typeof loadGCD_DPSTimelineReload !== 'undefined') {
			loadGCD_DPSTimelineReload(true);
			loadGCD_DPSTimelineReload = undefined;
		}

		if (typeof loadGCD_HPSTimelineReload !== 'undefined') {
			loadGCD_HPSTimelineReload(true);
			loadGCD_HPSTimelineReload = undefined;
		}
	}

	function loadDPSTimeLine() {
		let chart;

		const updateData = (dispose = false) => {
			if (typeof chart === 'undefined') return;
			if (typeof combat === 'undefined') return;

			if (dispose) {
				chart.dispose();
				return;
			}

			const data = combat.abilitiesApply
				.filter((e) => e.damage !== null && e.precast === false)
				.reduce(
					(acc, el) => [
						...acc,
						{
							ability: el.ability,
							timestamp: moment(el.timestamp, MOMENT_FORMAT),
							rawDamage: el.damage.value,
							damage: acc.length <= 0 ? el.damage.value : acc[acc.length - 1].damage + el.damage.value,
						},
					],
					[]
				);

			const dataChart = data.map((el, i) => ({
				duration: el.timestamp.diff(moment(combat.start, MOMENT_FORMAT), 'seconds', true),
				damage: el.rawDamage.toFixed(2),
				dps:
					i <= 0 ? 0 : (el.damage / el.timestamp.diff(moment(combat.start, MOMENT_FORMAT), 'seconds', true)).toFixed(2),
				additional: el.ability.substring(0, el.ability.indexOf('{')),
			}));

			chart.data = dataChart;

			return updateData;
		};

		am4core.ready(function () {
			// Themes begin
			am4core.useTheme(am4themes_dark);
			// Themes end

			// Create chart instance
			chart = am4core.create('chart-timeline', am4charts.XYChart);
			chart.scrollbarX = new am4core.Scrollbar();
			chart.scrollbarY = new am4core.Scrollbar();

			/* Create axes */
			const categoryAxis = chart.xAxes.push(new am4charts.CategoryAxis());
			categoryAxis.dataFields.category = 'duration';
			// categoryAxis.renderer.minGridDistance = 30;

			/* Create value axis */
			const valueAxis = chart.yAxes.push(new am4charts.ValueAxis());
			valueAxis.min = 0;

			/* Create series */
			const columnSeries = chart.series.push(new am4charts.ColumnSeries());
			columnSeries.name = 'Damage';
			columnSeries.dataFields.valueY = 'damage';
			columnSeries.dataFields.categoryX = 'duration';

			columnSeries.columns.template.tooltipText =
				'[#000 font-size: 15px]{name} in {categoryX}s:\n[/][#000 font-size: 20px]{valueY}[/] [#000]{additional}[/]';
			columnSeries.columns.template.propertyFields.fillOpacity = 'fillOpacity';

			const lineSeries = chart.series.push(new am4charts.LineSeries());
			lineSeries.name = 'DPS';
			lineSeries.dataFields.valueY = 'dps';
			lineSeries.dataFields.categoryX = 'duration';
			lineSeries.stroke = am4core.color('#c93c37');
			lineSeries.strokeWidth = 3;

			const bullet = lineSeries.bullets.push(new am4charts.Bullet());
			bullet.fill = am4core.color('#c93c37');
			bullet.tooltipText = '[#fff font-size: 15px]{name} in {categoryX}s:\n[/][#fff font-size: 20px]{valueY}[/]';

			const circle = bullet.createChild(am4core.Circle);
			circle.radius = 3;
			circle.fill = am4core.color('#fff');
			circle.strokeWidth = 4;

			chart.legend = new am4charts.Legend();

			loadDPSTimeLineReload = updateData();
		});

		return updateData;
	}

	function loadHPSTimeLine() {
		let chart;

		const updateData = (dispose = false) => {
			if (typeof chart === 'undefined') return;
			if (typeof combat === 'undefined') return;

			if (dispose) {
				chart.dispose();
				return;
			}

			const data = combat.abilitiesApply
				.filter((e) => e.heal !== null && e.precast === false)
				.reduce(
					(acc, el) => [
						...acc,
						{
							ability: el.ability,
							timestamp: moment(el.timestamp, MOMENT_FORMAT),
							rawHeal: el.heal.value,
							heal: acc.length <= 0 ? el.heal.value : acc[acc.length - 1].heal + el.heal.value,
						},
					],
					[]
				);

			const dataChart = data.map((el, i) => ({
				duration: el.timestamp.diff(moment(combat.start, MOMENT_FORMAT), 'seconds', true),
				heal: el.rawHeal.toFixed(2),
				hps:
					i <= 0 ? 0 : (el.heal / el.timestamp.diff(moment(combat.start, MOMENT_FORMAT), 'seconds', true)).toFixed(2),
				additional: el.ability.substring(0, el.ability.indexOf('{')),
			}));

			chart.data = dataChart;
			return updateData;
		};

		am4core.ready(function () {
			// Themes begin
			am4core.useTheme(am4themes_dark);
			// Themes end

			// Create chart instance
			chart = am4core.create('chart-timeline-heal', am4charts.XYChart);
			chart.scrollbarX = new am4core.Scrollbar();
			chart.scrollbarY = new am4core.Scrollbar();

			/* Create axes */
			const categoryAxis = chart.xAxes.push(new am4charts.CategoryAxis());
			categoryAxis.dataFields.category = 'duration';
			// categoryAxis.renderer.minGridDistance = 30;

			/* Create value axis */
			const valueAxis = chart.yAxes.push(new am4charts.ValueAxis());
			valueAxis.min = 0;

			/* Create series */
			const columnSeries = chart.series.push(new am4charts.ColumnSeries());
			columnSeries.name = 'Heal';
			columnSeries.dataFields.valueY = 'heal';
			columnSeries.dataFields.categoryX = 'duration';

			columnSeries.columns.template.tooltipText =
				'[#000 font-size: 15px]{name} in {categoryX}s:\n[/][#000 font-size: 20px]{valueY}[/] [#000]{additional}[/]';
			columnSeries.columns.template.propertyFields.fillOpacity = 'fillOpacity';

			const lineSeries = chart.series.push(new am4charts.LineSeries());
			lineSeries.name = 'HPS';
			lineSeries.dataFields.valueY = 'hps';
			lineSeries.dataFields.categoryX = 'duration';
			lineSeries.stroke = am4core.color('#347d39');
			lineSeries.strokeWidth = 3;

			const bullet = lineSeries.bullets.push(new am4charts.Bullet());
			bullet.fill = am4core.color('#347d39');
			bullet.tooltipText = '[#fff font-size: 15px]{name} in {categoryX}s:\n[/][#fff font-size: 20px]{valueY}[/]';

			const circle = bullet.createChild(am4core.Circle);
			circle.radius = 3;
			circle.fill = am4core.color('#fff');
			circle.strokeWidth = 4;

			chart.legend = new am4charts.Legend();

			loadHPSTimeLineReload = updateData();
		});

		return updateData;
	}

	function loadDTPSTimeLine() {
		let chart;

		const updateData = (dispose = false) => {
			if (typeof chart === 'undefined') return;
			if (typeof combat === 'undefined') return;

			if (dispose) {
				chart.dispose();
				return;
			}

			const data = combat.abilitiesTaken
				.filter((e) => e.damage !== null)
				.reduce(
					(acc, el) => [
						...acc,
						{
							ability: el.ability,
							timestamp: moment(el.timestamp, MOMENT_FORMAT),
							rawDamage: el.damage.value,
							damage: acc.length <= 0 ? el.damage.value : acc[acc.length - 1].damage + el.damage.value,
						},
					],
					[]
				);

			const dataChart = data.map((el, i) => ({
				duration: el.timestamp.diff(moment(combat.start, MOMENT_FORMAT), 'seconds', true),
				damage: el.rawDamage.toFixed(2),
				dtps:
					i <= 0 ? 0 : (el.damage / el.timestamp.diff(moment(combat.start, MOMENT_FORMAT), 'seconds', true)).toFixed(2),
				additional: el.ability.substring(0, el.ability.indexOf('{')),
			}));

			chart.data = dataChart;
			return updateData;
		};

		am4core.ready(function () {
			// Themes begin
			am4core.useTheme(am4themes_dark);
			// Themes end

			// Create chart instance
			chart = am4core.create('chart-timeline-damage-taken', am4charts.XYChart);
			chart.scrollbarX = new am4core.Scrollbar();
			chart.scrollbarY = new am4core.Scrollbar();

			/* Create axes */
			const categoryAxis = chart.xAxes.push(new am4charts.CategoryAxis());
			categoryAxis.dataFields.category = 'duration';
			// categoryAxis.renderer.minGridDistance = 30;

			/* Create value axis */
			const valueAxis = chart.yAxes.push(new am4charts.ValueAxis());
			valueAxis.min = 0;

			/* Create series */
			const columnSeries = chart.series.push(new am4charts.ColumnSeries());
			columnSeries.name = 'Damage';
			columnSeries.dataFields.valueY = 'damage';
			columnSeries.dataFields.categoryX = 'duration';

			columnSeries.columns.template.tooltipText =
				'[#000 font-size: 15px]{name} in {categoryX}s:\n[/][#000 font-size: 20px]{valueY}[/] [#000]{additional}[/]';
			columnSeries.columns.template.propertyFields.fillOpacity = 'fillOpacity';

			const lineSeries = chart.series.push(new am4charts.LineSeries());
			lineSeries.name = 'DTPS';
			lineSeries.dataFields.valueY = 'dtps';
			lineSeries.dataFields.categoryX = 'duration';
			lineSeries.stroke = am4core.color('#8e1519');
			lineSeries.strokeWidth = 3;

			const bullet = lineSeries.bullets.push(new am4charts.Bullet());
			bullet.fill = am4core.color('#8e1519');
			bullet.tooltipText = '[#fff font-size: 15px]{name} in {categoryX}s:\n[/][#fff font-size: 20px]{valueY}[/]';

			const circle = bullet.createChild(am4core.Circle);
			circle.radius = 3;
			circle.fill = am4core.color('#fff');
			circle.strokeWidth = 4;

			chart.legend = new am4charts.Legend();

			loadDTPSTimeLineReload = updateData();
		});

		return updateData;
	}

	function loadTPSTimeLine() {
		let chart;

		const updateData = (dispose = false) => {
			if (typeof chart === 'undefined') return;
			if (typeof combat === 'undefined') return;

			if (dispose) {
				chart.dispose();
				return;
			}

			const data = combat.abilitiesApply
				.filter((e) => (e.heal !== null || e.damage !== null) && e.precast === false)
				.reduce(
					(acc, el) => [
						...acc,
						{
							ability: el.ability,
							timestamp: moment(el.timestamp, MOMENT_FORMAT),
							rawThreat: el.threat,
							threat: acc.length <= 0 ? el.threat : acc[acc.length - 1].threat + el.threat,
						},
					],
					[]
				);

			const dataChart = data.map((el, i) => ({
				duration: el.timestamp.diff(moment(combat.start, MOMENT_FORMAT), 'seconds', true),
				threat: el.rawThreat.toFixed(2),
				tps:
					i <= 0 ? 0 : (el.threat / el.timestamp.diff(moment(combat.start, MOMENT_FORMAT), 'seconds', true)).toFixed(2),
				additional: el.ability.substring(0, el.ability.indexOf('{')),
			}));

			chart.data = dataChart;
			return updateData;
		};

		am4core.ready(function () {
			// Themes begin
			am4core.useTheme(am4themes_dark);
			// Themes end

			// Create chart instance
			chart = am4core.create('chart-timeline-threat', am4charts.XYChart);
			chart.scrollbarX = new am4core.Scrollbar();
			chart.scrollbarY = new am4core.Scrollbar();

			/* Create axes */
			const categoryAxis = chart.xAxes.push(new am4charts.CategoryAxis());
			categoryAxis.dataFields.category = 'duration';
			// categoryAxis.renderer.minGridDistance = 30;

			/* Create value axis */
			const valueAxis = chart.yAxes.push(new am4charts.ValueAxis());
			valueAxis.min = 0;

			/* Create series */
			const columnSeries = chart.series.push(new am4charts.ColumnSeries());
			columnSeries.name = 'Threat';
			columnSeries.dataFields.valueY = 'threat';
			columnSeries.dataFields.categoryX = 'duration';

			columnSeries.columns.template.tooltipText =
				'[#000 font-size: 15px]{name} in {categoryX}s:\n[/][#000 font-size: 20px]{valueY}[/] [#000]{additional}[/]';
			columnSeries.columns.template.propertyFields.fillOpacity = 'fillOpacity';

			const lineSeries = chart.series.push(new am4charts.LineSeries());
			lineSeries.name = 'TPS';
			lineSeries.dataFields.valueY = 'tps';
			lineSeries.dataFields.categoryX = 'duration';
			lineSeries.stroke = am4core.color('#966600');
			lineSeries.strokeWidth = 3;

			const bullet = lineSeries.bullets.push(new am4charts.Bullet());
			bullet.fill = am4core.color('#966600');
			bullet.tooltipText = '[#fff font-size: 15px]{name} in {categoryX}s:\n[/][#fff font-size: 20px]{valueY}[/]';

			const circle = bullet.createChild(am4core.Circle);
			circle.radius = 3;
			circle.fill = am4core.color('#fff');
			circle.strokeWidth = 4;

			chart.legend = new am4charts.Legend();

			loadTPSTimeLineReload = updateData();
		});

		return updateData;
	}

	function loadHitsGraphic() {
		let chart;

		const updateData = (dispose = false) => {
			if (typeof chart === 'undefined') return;
			if (typeof combat === 'undefined') return;

			if (dispose) {
				chart.dispose();
				return;
			}

			const data = combat.abilityTrigger.concat(combat.procsAndTicks);
			chart.data = data;
			return updateData;
		};

		am4core.ready(function () {
			// Themes begin
			am4core.useTheme(am4themes_dark);
			// Themes end

			chart = am4core.create('chart-hits', am4charts.XYChart);
			chart.padding(40, 40, 40, 40);

			const categoryAxis = chart.yAxes.push(new am4charts.CategoryAxis());
			categoryAxis.renderer.grid.template.location = 0;
			categoryAxis.dataFields.category = 'ability';
			categoryAxis.renderer.minGridDistance = 1;
			categoryAxis.renderer.inversed = true;
			categoryAxis.renderer.grid.template.disabled = true;

			const valueAxis = chart.xAxes.push(new am4charts.ValueAxis());
			valueAxis.min = 0;

			const series = chart.series.push(new am4charts.ColumnSeries());
			series.dataFields.categoryY = 'ability';
			series.dataFields.valueX = 'hits';
			series.tooltipText = '{valueX.value}';
			series.columns.template.strokeOpacity = 0;
			series.columns.template.column.cornerRadiusBottomRight = 5;
			series.columns.template.column.cornerRadiusTopRight = 5;

			const labelBullet = series.bullets.push(new am4charts.LabelBullet());
			labelBullet.label.horizontalCenter = 'left';
			labelBullet.label.dx = 10;
			labelBullet.label.text = "{values.valueX.workingValue.formatNumber('#.0as')}";
			labelBullet.locationX = 1;

			// as by default columns of the same series are of the same color, we add adapter which takes colors from chart.colors color set
			series.columns.template.adapter.add('fill', function (fill, target) {
				return chart.colors.getIndex(target.dataItem.index);
			});

			categoryAxis.sortBySeries = series;
			loadHitsGraphicReload = updateData();
		});

		return updateData;
	}

	function loadGCDPerAbility() {
		let chart;

		const updateData = (dispose = false) => {
			if (typeof chart === 'undefined') return;
			if (typeof combat === 'undefined') return;

			if (dispose) {
				chart.dispose();
				return;
			}

			const data = _.mapValues(_.groupBy(combat.gcds, 'ability'), (x) =>
				x.map((ability) => _.omit(ability, 'ability'))
			);

			chart.data = Object.keys(data).map((key) => ({
				ability: key,
				min: Math.min(...data[key].map((x) => x.GCD)),
				median: median(...data[key].map((x) => x.GCD)),
				max: Math.max(...data[key].map((x) => x.GCD)),
			}));
			return updateData;
		};

		am4core.ready(function () {
			// Themes begin
			am4core.useTheme(am4themes_dark);
			// Themes end

			chart = am4core.create('chart-gcd-per-ability', am4charts.XYChart);
			chart.colors.step = 2;

			chart.legend = new am4charts.Legend();
			chart.legend.position = 'top';
			chart.legend.paddingBottom = 20;
			chart.legend.labels.template.maxWidth = 95;

			const xAxis = chart.xAxes.push(new am4charts.CategoryAxis());
			xAxis.dataFields.category = 'ability';
			xAxis.renderer.cellStartLocation = 0.1;
			xAxis.renderer.cellEndLocation = 0.9;
			xAxis.renderer.grid.template.location = 0;

			const yAxis = chart.yAxes.push(new am4charts.ValueAxis());
			yAxis.min = 0;

			function createSeries(value, name) {
				const series = chart.series.push(new am4charts.ColumnSeries());
				series.dataFields.valueY = value;
				series.dataFields.categoryX = 'ability';
				series.name = name;
				series.columns.template.tooltipText =
					'[#000 font-size: 15px]{name} in {categoryX}:\n[/][#000 font-size: 20px]{valueY}[/]';

				const bullet = series.bullets.push(new am4charts.LabelBullet());
				bullet.interactionsEnabled = false;
				bullet.dy = 30;
				bullet.label.text = '{valueY}';
				bullet.label.fill = am4core.color('#ffffff');

				return series;
			}

			createSeries('min', 'Min');
			createSeries('median', 'Median');
			createSeries('max', 'Max');

			loadGCDPerAbilityReload = updateData();
		});

		return updateData;
	}

	function loadGCD_DPSTimeline() {
		let chart;

		const updateData = (dispose = false) => {
			if (typeof chart === 'undefined') return;
			if (typeof combat === 'undefined') return;

			if (dispose) {
				chart.dispose();
				return;
			}

			const dpsAndDamage = combat.abilitiesApply
				.filter((e) => e.damage !== null)
				.reduce(
					(acc, el) => [
						...acc,
						{
							timestamp: moment(el.timestamp, MOMENT_FORMAT),
							rawDamage: el.damage.value,
							damage: acc.length <= 0 ? el.damage.value : acc[acc.length - 1].damage + el.damage.value,
						},
					],
					[]
				);

			const finalData = combat.gcds
				.map((e) => ({ ...e, timestamp: moment(e.timestamp, MOMENT_FORMAT) }))
				.map((e) => ({
					ability: e.ability,
					timestamp: moment(e.timestamp, MOMENT_FORMAT),
					gcd: e.GCD,
					damage: dpsAndDamage
						.filter((e2) => e2.timestamp <= e.timestamp)
						.map((e) => e.rawDamage)
						.reduce((acc, el) => acc + el, 0),
					dps:
						dpsAndDamage
							.filter((e2) => e2.timestamp <= e.timestamp)
							.map((e) => e.rawDamage)
							.reduce((acc, el) => acc + el, 0) /
						e.timestamp.diff(moment(combat.start, MOMENT_FORMAT), 'seconds', true),
				}));

			chart.data = finalData.map((e, i) => ({
				count: (i + 1).toString(),
				gcd: e.gcd,
				dps: e.dps.toFixed(2),
				damage: e.damage,
				additional: e.ability,
			}));
			return updateData;
		};

		am4core.ready(function () {
			// Themes begin
			am4core.useTheme(am4themes_dark);
			// Themes end

			// Create chart instance
			chart = am4core.create('chart-gcd-timeline-dps', am4charts.XYChart);
			chart.scrollbarX = new am4core.Scrollbar();
			chart.scrollbarY = new am4core.Scrollbar();
			chart.colors.step = 2;

			// Create axes
			const categoryAxis = chart.xAxes.push(new am4charts.CategoryAxis());
			categoryAxis.dataFields.category = 'count';
			categoryAxis.title.text = 'GCD Used';

			// Create series
			function createAxisAndSeries(field, name, opposite, bullet_type) {
				const valueAxis = chart.yAxes.push(new am4charts.ValueAxis());
				valueAxis.min = 0;

				if (chart.yAxes.indexOf(valueAxis) != 0) {
					valueAxis.syncWithAxis = chart.yAxes.getIndex(0);
				}

				const series = chart.series.push(new am4charts.LineSeries());
				series.dataFields.valueY = field;
				series.dataFields.categoryX = 'count';
				series.strokeWidth = 2;
				series.yAxis = valueAxis;
				series.name = name;
				series.tooltipText = '{name}: [bold]{valueY}[/] [#000]{additional}[/]';
				series.tensionX = 0.8;
				series.showOnInit = true;

				const interfaceColors = new am4core.InterfaceColorSet();

				switch (bullet_type) {
					case 'triangle':
						{
							const bullet = series.bullets.push(new am4charts.Bullet());
							bullet.width = 12;
							bullet.height = 12;
							bullet.horizontalCenter = 'middle';
							bullet.verticalCenter = 'middle';

							const triangle = bullet.createChild(am4core.Triangle);
							triangle.stroke = interfaceColors.getFor('background');
							triangle.strokeWidth = 2;
							triangle.direction = 'top';
							triangle.width = 12;
							triangle.height = 12;
						}
						break;

					case 'rectangle':
						{
							const bullet = series.bullets.push(new am4charts.Bullet());
							bullet.width = 10;
							bullet.height = 10;
							bullet.horizontalCenter = 'middle';
							bullet.verticalCenter = 'middle';

							const rectangle = bullet.createChild(am4core.Rectangle);
							rectangle.stroke = interfaceColors.getFor('background');
							rectangle.strokeWidth = 2;
							rectangle.width = 10;
							rectangle.height = 10;
						}
						break;

					default:
						{
							const bullet = series.bullets.push(new am4charts.CircleBullet());
							bullet.circle.stroke = interfaceColors.getFor('background');
							bullet.circle.strokeWidth = 2;
						}
						break;
				}

				valueAxis.renderer.line.strokeOpacity = 1;
				valueAxis.renderer.line.strokeWidth = 2;
				valueAxis.renderer.line.stroke = series.stroke;
				valueAxis.renderer.labels.template.fill = series.stroke;
				valueAxis.renderer.opposite = opposite;
			}

			createAxisAndSeries('gcd', 'GCD', false, 'circle');
			createAxisAndSeries('dps', 'DPS', true, 'triangle');
			createAxisAndSeries('damage', 'Damage', true, 'rectangle');

			// Add legend
			chart.legend = new am4charts.Legend();
			chart.cursor = new am4charts.XYCursor();

			loadGCD_DPSTimelineReload = updateData();
		});

		return updateData;
	}

	function loadGCD_HPSTimeline() {
		let chart;

		const updateData = (dispose = false) => {
			if (typeof chart === 'undefined') return;
			if (typeof combat === 'undefined') return;

			if (dispose) {
				chart.dispose();
				return;
			}

			const hpsAndHeal = combat.abilitiesApply
				.filter((e) => e.heal !== null)
				.reduce(
					(acc, el) => [
						...acc,
						{
							timestamp: moment(el.timestamp, MOMENT_FORMAT),
							rawHeal: el.heal.value,
							heal: acc.length <= 0 ? el.heal.value : acc[acc.length - 1].heal + el.heal.value,
						},
					],
					[]
				);

			const finalData = combat.gcds
				.map((e) => ({ ...e, timestamp: moment(e.timestamp, MOMENT_FORMAT) }))
				.map((e) => ({
					ability: e.ability,
					timestamp: moment(e.timestamp, MOMENT_FORMAT),
					gcd: e.GCD,
					heal: hpsAndHeal
						.filter((e2) => e2.timestamp <= e.timestamp)
						.map((e) => e.rawHeal)
						.reduce((acc, el) => acc + el, 0),
					hps:
						hpsAndHeal
							.filter((e2) => e2.timestamp <= e.timestamp)
							.map((e) => e.rawHeal)
							.reduce((acc, el) => acc + el, 0) /
						e.timestamp.diff(moment(combat.start, MOMENT_FORMAT), 'seconds', true),
				}));

			chart.data = finalData.map((e, i) => ({
				count: (i + 1).toString(),
				gcd: e.gcd,
				hps: e.hps.toFixed(2),
				heal: e.heal,
				additional: e.ability,
			}));
			return updateData;
		};

		am4core.ready(function () {
			// Themes begin
			am4core.useTheme(am4themes_dark);
			// Themes end

			// Create chart instance
			chart = am4core.create('chart-gcd-timeline-hps', am4charts.XYChart);
			chart.scrollbarX = new am4core.Scrollbar();
			chart.scrollbarY = new am4core.Scrollbar();
			chart.colors.step = 2;

			// Create axes
			const categoryAxis = chart.xAxes.push(new am4charts.CategoryAxis());
			categoryAxis.dataFields.category = 'count';
			categoryAxis.title.text = 'GCD Used';

			// Create series
			function createAxisAndSeries(field, name, opposite, bullet_type) {
				const valueAxis = chart.yAxes.push(new am4charts.ValueAxis());
				valueAxis.min = 0;

				if (chart.yAxes.indexOf(valueAxis) != 0) {
					valueAxis.syncWithAxis = chart.yAxes.getIndex(0);
				}

				const series = chart.series.push(new am4charts.LineSeries());
				series.dataFields.valueY = field;
				series.dataFields.categoryX = 'count';
				series.strokeWidth = 2;
				series.yAxis = valueAxis;
				series.name = name;
				series.tooltipText = '{name}: [bold]{valueY}[/] [#000]{additional}[/]';
				series.tensionX = 0.8;
				series.showOnInit = true;

				const interfaceColors = new am4core.InterfaceColorSet();

				switch (bullet_type) {
					case 'triangle':
						{
							const bullet = series.bullets.push(new am4charts.Bullet());
							bullet.width = 12;
							bullet.height = 12;
							bullet.horizontalCenter = 'middle';
							bullet.verticalCenter = 'middle';

							const triangle = bullet.createChild(am4core.Triangle);
							triangle.stroke = interfaceColors.getFor('background');
							triangle.strokeWidth = 2;
							triangle.direction = 'top';
							triangle.width = 12;
							triangle.height = 12;
						}
						break;

					case 'rectangle':
						{
							const bullet = series.bullets.push(new am4charts.Bullet());
							bullet.width = 10;
							bullet.height = 10;
							bullet.horizontalCenter = 'middle';
							bullet.verticalCenter = 'middle';

							const rectangle = bullet.createChild(am4core.Rectangle);
							rectangle.stroke = interfaceColors.getFor('background');
							rectangle.strokeWidth = 2;
							rectangle.width = 10;
							rectangle.height = 10;
						}
						break;

					default:
						{
							const bullet = series.bullets.push(new am4charts.CircleBullet());
							bullet.circle.stroke = interfaceColors.getFor('background');
							bullet.circle.strokeWidth = 2;
						}
						break;
				}

				valueAxis.renderer.line.strokeOpacity = 1;
				valueAxis.renderer.line.strokeWidth = 2;
				valueAxis.renderer.line.stroke = series.stroke;
				valueAxis.renderer.labels.template.fill = series.stroke;
				valueAxis.renderer.opposite = opposite;
			}

			createAxisAndSeries('gcd', 'GCD', false, 'circle');
			createAxisAndSeries('hps', 'HPS', true, 'triangle');
			createAxisAndSeries('heal', 'Heal', true, 'rectangle');

			// Add legend
			chart.legend = new am4charts.Legend();
			chart.cursor = new am4charts.XYCursor();

			loadGCD_HPSTimelineReload = updateData();
		});

		return updateData;
	}

	async function toggleParse() {
		realTimeParse = !realTimeParse;

		if (realTimeParse) {
			resetDashboard();
			noFileSelected = true;
			await loadFiles();
			await tick();

			const _files = files
				.map((e) => parseToDate(e))
				.filter((e) => e !== null)
				.sort((a, b) => a.dateTime.unix() - b.dateTime.unix());

			if (_files.length > 0) {
				selectedFile = _files[_files.length - 1].file;

				parse.postMessage(
					JSON.stringify({
						type: 'real_time',
						path: path,
						selectedFile: selectedFile,
					})
				);
				ipcRenderer.invoke('start-parse');
			}
		} else {
			parse.postMessage(JSON.stringify({ type: 'stop_real_time' }));
		}
	}

	function parseToDate(file) {
		if (!file.includes('combat_')) {
			return null;
		}

		const name = file.replace('combat_', '').replace('.txt', '').trim();
		const date = name.substring(0, name.indexOf('_')).trim();
		const time = name.substring(name.indexOf('_') + 1, name.length).trim();
		const m = moment(
			date +
				' ' +
				time.substring(0, 2) +
				':' +
				time.substring(3, 5) +
				':' +
				time.substring(6, 8) +
				'.' +
				time.substring(9),
			MOMENT_FORMAT
		);
		return m.isValid()
			? {
					dateTime: m,
					file: file,
			  }
			: null;
	}

	function interpretCombatFileName(file) {
		const m = parseToDate(file);
		return m !== null ? m.dateTime.format('LLLL') : file;
	}

	async function loadFiles() {
		try {
			files = await fsPromises.readdir(path);
			logs = {};
			files.forEach((e) => {
				logs[e] = null;
			});
		} catch (err) {
			console.error(err);
		}
	}

	(async function () {
		parse.onmessage = async (e) => {
			const message = JSON.parse(e.data);

			switch (message.type) {
				case 'ready_current_combat':
					{
						parse.postMessage(JSON.stringify({ type: 'current_combat' }));
					}
					break;

				case 'current_combat':
					{
						parse.postMessage(JSON.stringify({ type: 'current_combat' }));

						if (message.data.length > 0 && message.data[0].id !== currentCombatHasChanges) {
							currentCombatHasChanges = message.data[0].id;
							await loadStats(message.data[0]);
						}
					}
					break;

				case 'parse_logs':
					{
						const file = message.data.file;
						logs[file] = message.data.stats;
						await loadStats(logs[file][0]);
						loading = false;
					}
					break;

				case 'parse_log_percentage':
					{
						percentage = message.data;
					}
					break;
			}
		};

		await loadFiles();
	})();
</script>

{#if loading}
	<Loading bind:value={percentage} />
{/if}

<!-- Header -->
<div
	class="Layout Layout--sidebarPosition-flowRow-start Layout--gutter-none"
	style="position: sticky; top: 0; z-index: 10;">
	<div class="Layout-main">
		<div class="Header color-shadow-medium">
			<div class="Header-item">
				<a href="#" class="Header-link f4 d-flex flex-items-center">
					<img src="logo.svg" style="height: 32px;" class="mr-2" />
					<span class="poppins">Trace Parse</span>
				</a>
			</div>

			<div class="Header-item Header-item--full" />
			<div class="Header-item">
				<a href="#" class="Header-link" on:click={() => onSelectedMenuItem('analyze')}> Analyze Logs </a>
			</div>
			<!-- <div class="Header-item">
				<a href="#" class="Header-link">Raid Team</a>
			</div> -->
			<div class="Header-item">
				<a href="#" class="Header-link" on:click={() => onSelectedMenuItem('settings')}>Preferences</a>
			</div>
			<div class="Header-item">
				<button class="btn btn-outline" type="button" aria-selected={realTimeParse} on:click={toggleParse}
					>Parse</button>
			</div>
		</div>
	</div>
</div>

<!-- Main -->
{#if selectedMenu === 'analyze'}
	<div class="Layout Layout--gutter-none p-4">
		<div class="Layout-main">
			<!-- Subhead -->
			<div class="Subhead">
				<div class="Subhead-heading">Analyze Logs</div>
				{#if noFileSelected}
					<div class="Subhead-actions">
						<div class="d-flex flex-justify-end position-relative">
							<form>
								<select
									class="form-select"
									aria-label="Combat logs"
									disabled={realTimeParse}
									bind:value={selectedFile}
									on:blur={selectCombat}
									on:change={selectCombat}>
									<option values="">Select a combat log</option>

									{#each files as file}
										<option value={file}>{interpretCombatFileName(file)}</option>
									{/each}
								</select>
							</form>
						</div>
					</div>
				{/if}
				<div class="Subhead-description">Get statistical information from your combat encounters.</div>
			</div>

			<!-- Stats -->
			<div class="Layout-main">
				<div class="mt-4">
					{#if typeof combat !== 'undefined'}
						<!-- Subhead -->
						<div class="d-flex flex-justify-between flex-row flex-items-center">
							<div>
								<div class="h1 lh-condensed-ultra p-1">
									{combat.battle.replace(/\(|\)/g, '')}
								</div>
								<div class="f4 lh-condensed-ultra p-1">
									{#if combat.health !== null}
										<p>Health: {combat.health.toLocaleString()}</p>
									{/if}

									<p>
										Targets: {[...new Set(combat.targets.map((e) => e.substring(0, e.indexOf('{')).trim()))].join(', ')}
									</p>
									<p>
										Start: {moment(combat.start, MOMENT_FORMAT).format('HH:mm:ss.SSS')}
										Ended: {moment(combat.end, MOMENT_FORMAT).format('HH:mm:ss.SSS')}
									</p>
								</div>
							</div>

							<details class="details-reset details-overlay" open>
								<summary class="btn btn-large" aria-haspopup="true">
									{combat.player.replace('@', '')} ({combat.battle.replace(/\(|\)/g, '')})
									<br />
									<span class="f6">{moment(combat.start, MOMENT_FORMAT).format('HH:mm:ss.SSS')} - </span>
									<span class="f6">{moment(combat.end, MOMENT_FORMAT).format('HH:mm:ss.SSS')} - </span>
									<span class="f6">{combat.duration}</span>
								</summary>
								<div class="SelectMenu right-0">
									<div class="SelectMenu-modal">
										<div class="SelectMenu-list">
											{#if Array.isArray(logs[selectedFile])}
												{#each logs[selectedFile] as stat}
													<button
														class="SelectMenu-item"
														role="menuitemcheckbox"
														on:click={async () => await loadStats(stat)}
														aria-checked={stat.id === combat.id}>
														<svg
															class="SelectMenu-icon SelectMenu-icon--check octicon octicon-check"
															xmlns="http://www.w3.org/2000/svg"
															viewBox="0 0 16 16"
															width="16"
															height="16">
															<path
																fill-rule="evenodd"
																clip-rule="evenodd"
																d="M13.78 4.22C13.9204 4.36062 13.9993 4.55125 13.9993 4.75C13.9993 4.94875 13.9204 5.13937 13.78 5.28L6.53 12.53C6.38937 12.6704 6.19875 12.7493 6 12.7493C5.80125 12.7493 5.61062 12.6704 5.47 12.53L2.22 9.28C2.08752 9.13782 2.0154 8.94978 2.01882 8.75547C2.02225 8.56117 2.10096 8.37579 2.23838 8.23837C2.37579 8.10096 2.56118 8.02225 2.75548 8.01882C2.94978 8.01539 3.13782 8.08752 3.28 8.22L6 10.94L12.72 4.22C12.8606 4.07955 13.0512 4.00066 13.25 4.00066C13.4487 4.00066 13.6394 4.07955 13.78 4.22Z" /></svg>
														<div>
															{stat.player.replace('@', '')} ({stat.battle.replace(/\(|\)/g, '')})
															<br />
															<span class="f6">{moment(stat.start, MOMENT_FORMAT).format('HH:mm:ss.SSS')} - </span>
															<span class="f6">{moment(stat.end, MOMENT_FORMAT).format('HH:mm:ss.SSS')} - </span>
															<span class="f6">{stat.duration}</span>
														</div>
													</button>
												{/each}
											{/if}
										</div>
									</div>
								</div>
							</details>
						</div>

						<!-- Combat summary -->
						<div class="mt-4 d-flex flex-row flex-justify-around color-bg-primary p-2 Box rounded-3">
							<div class="text-center">
								<p class="h2 lh-condensed-ultra">{combat.duration}</p>
								<p class="f4 lh-condensed-ultra">Duration</p>
							</div>

							<div class="text-center">
								<p class="h2 lh-condensed-ultra">{combat.apm.toFixed(2)}</p>
								<p class="f4 lh-condensed-ultra">APM</p>
							</div>

							<div class="text-center">
								<p class="h2 lh-condensed-ultra">{combat.hits}</p>
								<p class="f4 lh-condensed-ultra">Hits</p>
							</div>

							<div class="text-center">
								<p class="h2 lh-condensed-ultra">
									{combat.gcdMedian.toLocaleString(undefined, {
										minimumFractionDigits: 2,
										maximumFractionDigits: 2,
									})}
								</p>
								<p class="f4 lh-condensed-ultra">GCD Median</p>
							</div>
						</div>

						<!-- Damage/Heal charts -->
						<div class="mt-4 d-flex flex-column flex-lg-row flex-items-center flex-lg-items-center">
							<div class="col-12 col-lg-6">
								<div class="Box d-flex flex-row flex-justify-around flex-items-center p-2">
									<div class="text-center">
										<p class="h2 lh-condensed-ultra">
											{combat.damage.toLocaleString(undefined, {
												minimumFractionDigits: 2,
												maximumFractionDigits: 2,
											})}
										</p>
										<p class="f4 lh-condensed-ultra">Damage</p>
									</div>

									<div class="text-center">
										<p class="h2 lh-condensed-ultra">
											{combat.dps.toLocaleString(undefined, {
												minimumFractionDigits: 2,
												maximumFractionDigits: 2,
											})}
										</p>
										<p class="f4 lh-condensed-ultra">DPS</p>
									</div>
								</div>

								<div class="Box mt-4">
									<div id="chart-timeline" class="chart" />
								</div>
							</div>

							<div class="col-12 col-lg-6 ml-lg-4">
								<div class="Box d-flex flex-row flex-justify-around flex-items-center p-2">
									<div class="text-center">
										<p class="h2 lh-condensed-ultra">
											{combat.heals.toLocaleString(undefined, {
												minimumFractionDigits: 2,
												maximumFractionDigits: 2,
											})}
										</p>
										<p class="f4 lh-condensed-ultra">Heals</p>
									</div>

									<div class="text-center">
										<p class="h2 lh-condensed-ultra">
											{combat.hps.toLocaleString(undefined, {
												minimumFractionDigits: 2,
												maximumFractionDigits: 2,
											})}
										</p>
										<p class="f4 lh-condensed-ultra">HPS</p>
									</div>
								</div>

								<div class="Box mt-4">
									<div id="chart-timeline-heal" class="chart" />
								</div>
							</div>
						</div>

						<!-- Damage Taken/Threat charts -->
						<div class="mt-4 d-flex flex-column flex-lg-row flex-items-center flex-lg-items-center">
							<div class="col-12 col-lg-6">
								<div class="Box d-flex flex-row flex-justify-around flex-items-center p-2">
									<div class="text-center">
										<p class="h2 lh-condensed-ultra">
											{combat.damageTaken.toLocaleString(undefined, {
												minimumFractionDigits: 2,
												maximumFractionDigits: 2,
											})}
										</p>
										<p class="f4 lh-condensed-ultra">Damage Taken</p>
									</div>

									<div class="text-center">
										<p class="h2 lh-condensed-ultra">
											{combat.dtps.toLocaleString(undefined, {
												minimumFractionDigits: 2,
												maximumFractionDigits: 2,
											})}
										</p>
										<p class="f4 lh-condensed-ultra">DTPS</p>
									</div>
								</div>

								<div class="Box mt-4">
									<div id="chart-timeline-damage-taken" class="chart" />
								</div>
							</div>

							<div class="col-12 col-lg-6 ml-lg-4">
								<div class="Box d-flex flex-row flex-justify-around flex-items-center p-2">
									<div class="text-center">
										<p class="h2 lh-condensed-ultra">
											{combat.threat.toLocaleString(undefined, {
												minimumFractionDigits: 2,
												maximumFractionDigits: 2,
											})}
										</p>
										<p class="f4 lh-condensed-ultra">Threat</p>
									</div>

									<div class="text-center">
										<p class="h2 lh-condensed-ultra">
											{combat.tps.toLocaleString(undefined, {
												minimumFractionDigits: 2,
												maximumFractionDigits: 2,
											})}
										</p>
										<p class="f4 lh-condensed-ultra">TPS</p>
									</div>
								</div>

								<div class="Box mt-4">
									<div id="chart-timeline-threat" class="chart" />
								</div>
							</div>
						</div>

						<!-- Chart hits -->
						<div class="mt-4">
							<div class="Box col-12">
								<div id="chart-hits" class="chart" />
							</div>
						</div>

						<!-- GCD Summary -->
						<div class="mt-4 d-flex flex-row flex-justify-around color-bg-primary p-2 Box rounded-3">
							<div class="text-center">
								<p class="h2 lh-condensed-ultra">{combat.gcds.length}</p>
								<p class="f4 lh-condensed-ultra">GCDs Used</p>
							</div>

							<div class="text-center">
								<p class="h2 lh-condensed-ultra">
									{combat.gcdMedian.toLocaleString(undefined, {
										minimumFractionDigits: 2,
										maximumFractionDigits: 2,
									})}
								</p>
								<p class="f4 lh-condensed-ultra">GCD Median</p>
							</div>

							<div class="text-center">
								<p class="h2 lh-condensed-ultra">
									{combat.gcdMean.toLocaleString(undefined, {
										minimumFractionDigits: 2,
										maximumFractionDigits: 2,
									})}
								</p>
								<p class="f4 lh-condensed-ultra">GCD Average</p>
							</div>

							<div class="text-center">
								<p class="h2 lh-condensed-ultra">
									{combat.gcdMin.toLocaleString(undefined, {
										minimumFractionDigits: 2,
										maximumFractionDigits: 2,
									})}
								</p>
								<p class="f4 lh-condensed-ultra">GCD Min</p>
							</div>

							<div class="text-center">
								<p class="h2 lh-condensed-ultra">
									{combat.gcdMax.toLocaleString(undefined, {
										minimumFractionDigits: 2,
										maximumFractionDigits: 2,
									})}
								</p>
								<p class="f4 lh-condensed-ultra">GCD Max</p>
							</div>
						</div>

						<!-- GCD per ability -->
						<div class="mt-4">
							<div class="Box col-12">
								<div id="chart-gcd-per-ability" class="chart" />
							</div>
						</div>

						<!-- DPS GCD timeline -->
						<div class="d-flex flex-column flex-lg-row flex-items-center flex-lg-items-center">
							<div class="col-12 col-lg-6">
								<div class="Box mt-4">
									<div id="chart-gcd-timeline-dps" class="chart" />
								</div>
							</div>

							<div class="col-12 col-lg-6 ml-lg-4">
								<div class="Box mt-4">
									<div id="chart-gcd-timeline-hps" class="chart" />
								</div>
							</div>
						</div>

						<!-- Detail -->
						<div class="mt-4 border rounded-3">
							<nav class="UnderlineNav">
								<div class="UnderlineNav-body" role="tablist">
									<button
										on:click={() => (selectedTab = 'damage')}
										class="UnderlineNav-item"
										role="tab"
										type="button"
										aria-selected={selectedTab === 'damage'}>
										Damage
									</button>
									<button
										on:click={() => (selectedTab = 'heal')}
										class="UnderlineNav-item"
										role="tab"
										type="button"
										aria-selected={selectedTab === 'heal'}>
										Heals
									</button>
								</div>
							</nav>

							{#if selectedTab === 'damage'}
								<div class="Box--condensed" style="background: #2d333b !important;">
									<div class="Box-row rounded-0" style="background: #21262d !important;">
										<h5>Trigger abilities</h5>
									</div>

									<ul class="Box-row d-flex flex-row flex-justify-between rounded-0">
										<li class="col-3 text-bold">Ability</li>

										<li class="col-1 text-bold">Type</li>
										<li class="col-1 text-bold">Hits</li>

										<li class="col-1 text-bold">DMG</li>
										<li class="col-1 text-bold">DPS</li>

										<li class="col-1 text-bold">Damage %</li>
										<li class="col-1 text-bold">Crit. Hits %</li>
										<li class="col-1 text-bold">Miss Hits %</li>
									</ul>

									{#each combat.abilityTrigger.filter((e) => e.type !== 'unknown') as ability}
										<div class="Box-row d-flex flex-row flex-justify-between rounded-0">
											<div class="h4 col-3">
												{ability.ability}
												{#if ability.precast}
													<span class="f3-light">(Precast)</span>
												{/if}
											</div>

											<div class="col-1 capitalize">{ability.type}</div>
											<div class="col-1">{ability.hits}</div>

											<div class="col-1">
												{ability.damage.toLocaleString(undefined, {
													minimumFractionDigits: 2,
													maximumFractionDigits: 2,
												})}
											</div>
											<div class="col-1">
												{ability.dps.toLocaleString(undefined, {
													minimumFractionDigits: 2,
													maximumFractionDigits: 2,
												})}
											</div>

											<div class="col-1">
												{ability.damagePercentage.toFixed(2)}%
											</div>
											<div class="col-1">
												{ability.criticalHitsPercentage.toFixed(2)}%
											</div>
											<div class="col-1">
												{ability.missHitsPercentage.toFixed(2)}%
											</div>
										</div>
									{/each}

									<ul
										class="Box-row d-flex flex-row flex-justify-between rounded-0"
										style="background: #ffb8b0 !important; color: #22272e !important;">
										<li class="h4 col-3 text-bold">Sub-Total</li>

										<li class="col-1 capitalize" />
										<li class="col-1 text-bold">
											{combat.abilityTrigger.filter((e) => e.type !== 'unknown').reduce((acc, el) => acc + el.hits, 0)}
										</li>

										<li class="col-1 text-bold">
											{combat.abilityTrigger
												.filter((e) => e.type !== 'unknown')
												.reduce((acc, el) => acc + el.damage, 0)
												.toLocaleString(undefined, {
													minimumFractionDigits: 2,
													maximumFractionDigits: 2,
												})}
										</li>
										<li class="col-1 text-bold">
											{combat.abilityTrigger
												.filter((e) => e.type !== 'unknown')
												.reduce((acc, el) => acc + el.dps, 0)
												.toLocaleString(undefined, {
													minimumFractionDigits: 2,
													maximumFractionDigits: 2,
												})}
										</li>

										<li class="col-1 text-bold">
											{combat.abilityTrigger
												.filter((e) => e.type !== 'unknown')
												.reduce((acc, el) => acc + el.damagePercentage, 0)
												.toFixed(2)}%
										</li>
										<li class="col-1 text-bold">
											{(
												(combat.abilityTrigger
													.filter((e) => e.type !== 'unknown')
													.reduce((acc, el) => acc + el.criticalHits, 0) /
													combat.abilityTrigger
														.filter((e) => e.type !== 'unknown')
														.reduce((acc, el) => acc + el.hits, 0)) *
												100
											).toFixed(2)}%
										</li>
										<li class="col-1 text-bold">
											{(
												(combat.abilityTrigger
													.filter((e) => e.type !== 'unknown')
													.reduce((acc, el) => acc + el.missHits, 0) /
													combat.abilityTrigger
														.filter((e) => e.type !== 'unknown')
														.reduce((acc, el) => acc + el.hits, 0)) *
												100
											).toFixed(2)}%
										</li>
									</ul>

									<div class="Box-row rounded-0" style="background: #21262d !important;">
										<h5>Procs and Ticks</h5>
									</div>

									{#each combat.procsAndTicks.filter((e) => e.type !== 'unknown') as ability}
										<div class="Box-row d-flex flex-row flex-justify-between rounded-0">
											<div class="h4 col-3">{ability.ability}</div>

											<div class="col-1 capitalize">{ability.type}</div>
											<div class="col-1">{ability.hits}</div>

											<div class="col-1">
												{ability.damage.toLocaleString(undefined, {
													minimumFractionDigits: 2,
													maximumFractionDigits: 2,
												})}
											</div>
											<div class="col-1">
												{ability.dps.toLocaleString(undefined, {
													minimumFractionDigits: 2,
													maximumFractionDigits: 2,
												})}
											</div>

											<div class="col-1">
												{ability.damagePercentage.toFixed(2)}%
											</div>
											<div class="col-1">
												{ability.criticalHitsPercentage.toFixed(2)}%
											</div>
											<div class="col-1">
												{ability.missHitsPercentage.toFixed(2)}%
											</div>
										</div>
									{/each}

									<ul
										class="Box-row d-flex flex-row flex-justify-between rounded-0"
										style="background: #ffb8b0 !important; color: #22272e !important;">
										<li class="h4 col-3 text-bold">Sub-Total</li>

										<li class="col-1 capitalize" />
										<li class="col-1 text-bold">
											{combat.procsAndTicks.filter((e) => e.type !== 'unknown').reduce((acc, el) => acc + el.hits, 0)}
										</li>

										<li class="col-1 text-bold">
											{combat.procsAndTicks
												.filter((e) => e.type !== 'unknown')
												.reduce((acc, el) => acc + el.damage, 0)
												.toLocaleString(undefined, {
													minimumFractionDigits: 2,
													maximumFractionDigits: 2,
												})}
										</li>
										<li class="col-1 text-bold">
											{combat.procsAndTicks
												.filter((e) => e.type !== 'unknown')
												.reduce((acc, el) => acc + el.dps, 0)
												.toLocaleString(undefined, {
													minimumFractionDigits: 2,
													maximumFractionDigits: 2,
												})}
										</li>

										<li class="col-1 text-bold">
											{combat.procsAndTicks
												.filter((e) => e.type !== 'unknown')
												.reduce((acc, el) => acc + el.damagePercentage, 0)
												.toFixed(2)}%
										</li>
										<li class="col-1 text-bold">
											{(
												(combat.procsAndTicks
													.filter((e) => e.type !== 'unknown')
													.reduce((acc, el) => acc + el.criticalHits, 0) /
													combat.procsAndTicks
														.filter((e) => e.type !== 'unknown')
														.reduce((acc, el) => acc + el.hits, 0)) *
												100
											).toFixed(2)}%
										</li>
										<li class="col-1 text-bold">
											{(
												(combat.procsAndTicks
													.filter((e) => e.type !== 'unknown')
													.reduce((acc, el) => acc + el.missHits, 0) /
													combat.procsAndTicks
														.filter((e) => e.type !== 'unknown')
														.reduce((acc, el) => acc + el.hits, 0)) *
												100
											).toFixed(2)}%
										</li>
									</ul>

									<ul
										class="Box-row d-flex flex-row flex-justify-between rounded-bottom-3"
										style="background: #922323 !important; color: #cdd9e5 !important">
										<li class="h4 col-3 text-bold">Total</li>

										<li class="col-1 capitalize" />
										<li class="col-1 text-bold">
											{combat.procsAndTicks
												.concat(combat.abilityTrigger)
												.filter((e) => e.type !== 'unknown')
												.reduce((acc, el) => acc + el.hits, 0)}
										</li>

										<li class="col-1 text-bold">
											{combat.procsAndTicks
												.concat(combat.abilityTrigger)
												.filter((e) => e.type !== 'unknown')
												.reduce((acc, el) => acc + el.damage, 0)
												.toLocaleString(undefined, {
													minimumFractionDigits: 2,
													maximumFractionDigits: 2,
												})}
										</li>
										<li class="col-1 text-bold">
											{combat.procsAndTicks
												.concat(combat.abilityTrigger)
												.filter((e) => e.type !== 'unknown')
												.reduce((acc, el) => acc + el.dps, 0)
												.toLocaleString(undefined, {
													minimumFractionDigits: 2,
													maximumFractionDigits: 2,
												})}
										</li>

										<li class="col-1 text-bold">
											{combat.procsAndTicks
												.concat(combat.abilityTrigger)
												.filter((e) => e.type !== 'unknown')
												.reduce((acc, el) => acc + el.damagePercentage, 0)
												.toFixed(2)}%
										</li>
										<li class="col-1 text-bold">
											{(
												(combat.procsAndTicks
													.concat(combat.abilityTrigger)
													.filter((e) => e.type !== 'unknown')
													.reduce((acc, el) => acc + el.criticalHits, 0) /
													combat.procsAndTicks
														.concat(combat.abilityTrigger)
														.filter((e) => e.type !== 'unknown')
														.reduce((acc, el) => acc + el.hits, 0)) *
												100
											).toFixed(2)}%
										</li>
										<li class="col-1 text-bold">
											{(
												(combat.procsAndTicks
													.concat(combat.abilityTrigger)
													.filter((e) => e.type !== 'unknown')
													.reduce((acc, el) => acc + el.missHits, 0) /
													combat.procsAndTicks
														.concat(combat.abilityTrigger)
														.filter((e) => e.type !== 'unknown')
														.reduce((acc, el) => acc + el.hits, 0)) *
												100
											).toFixed(2)}%
										</li>
									</ul>
								</div>
							{:else}
								<div class="Box--condensed" style="background: #2d333b !important;">
									<div class="Box-row rounded-0" style="background: #21262d !important;">
										<h5>Trigger abilities</h5>
									</div>

									<ul class="Box-row d-flex flex-row flex-justify-between rounded-0">
										<li class="col-3 text-bold">Ability</li>

										<li class="col-1 text-bold">Hits</li>

										<li class="col-1 text-bold">Heal</li>
										<li class="col-1 text-bold">HPS</li>

										<li class="col-1 text-bold">Heal %</li>
										<li class="col-1 text-bold">Crit. Hits %</li>
									</ul>

									{#each combat.abilityTrigger.filter((e) => e.type === 'unknown') as ability}
										<div class="Box-row d-flex flex-row flex-justify-between rounded-0">
											<div class="h4 col-3">
												{ability.ability}
												{#if ability.precast}
													<span class="f3-light">(Precast)</span>
												{/if}
											</div>

											<div class="col-1">{ability.hits}</div>

											<div class="col-1">
												{ability.heals.toLocaleString(undefined, {
													minimumFractionDigits: 2,
													maximumFractionDigits: 2,
												})}
											</div>
											<div class="col-1">
												{ability.hps.toLocaleString(undefined, {
													minimumFractionDigits: 2,
													maximumFractionDigits: 2,
												})}
											</div>

											<div class="col-1">
												{ability.healPercentage.toFixed(2)}%
											</div>
											<div class="col-1">
												{ability.criticalHitsPercentage.toFixed(2)}%
											</div>
										</div>
									{/each}

									<ul
										class="Box-row d-flex flex-row flex-justify-between rounded-0"
										style="background: #b4f1b4 !important; color: #22272e !important;">
										<li class="h4 col-3 text-bold">Sub-Total</li>

										<li class="col-1 text-bold">
											{combat.abilityTrigger.filter((e) => e.type === 'unknown').reduce((acc, el) => acc + el.hits, 0)}
										</li>

										<li class="col-1 text-bold">
											{combat.abilityTrigger
												.filter((e) => e.type === 'unknown')
												.reduce((acc, el) => acc + el.heals, 0)
												.toLocaleString(undefined, {
													minimumFractionDigits: 2,
													maximumFractionDigits: 2,
												})}
										</li>
										<li class="col-1 text-bold">
											{combat.abilityTrigger
												.filter((e) => e.type === 'unknown')
												.reduce((acc, el) => acc + el.hps, 0)
												.toLocaleString(undefined, {
													minimumFractionDigits: 2,
													maximumFractionDigits: 2,
												})}
										</li>

										<li class="col-1 text-bold">
											{combat.abilityTrigger
												.filter((e) => e.type === 'unknown')
												.reduce((acc, el) => acc + el.healPercentage, 0)
												.toFixed(2)}%
										</li>
										<li class="col-1 text-bold">
											{#if combat.abilityTrigger.filter((e) => e.type === 'unknown').length <= 0}
												0.00%
											{:else}
												{(
													(combat.abilityTrigger
														.filter((e) => e.type === 'unknown')
														.reduce((acc, el) => acc + el.criticalHits, 0) /
														combat.abilityTrigger
															.filter((e) => e.type === 'unknown')
															.reduce((acc, el) => acc + el.hits, 0)) *
													100
												).toFixed(2)}%
											{/if}
										</li>
									</ul>

									<div class="Box-row rounded-0" style="background: #21262d !important;">
										<h5>Procs and Ticks</h5>
									</div>

									{#each combat.procsAndTicks.filter((e) => e.type === 'unknown') as ability}
										<div class="Box-row d-flex flex-row flex-justify-between rounded-0">
											<div class="h4 col-3">{ability.ability}</div>

											<div class="col-1">{ability.hits}</div>

											<div class="col-1">
												{ability.heals.toLocaleString(undefined, {
													minimumFractionDigits: 2,
													maximumFractionDigits: 2,
												})}
											</div>
											<div class="col-1">
												{ability.hps.toLocaleString(undefined, {
													minimumFractionDigits: 2,
													maximumFractionDigits: 2,
												})}
											</div>

											<div class="col-1">
												{ability.damagePercentage.toFixed(2)}%
											</div>
											<div class="col-1">
												{ability.criticalHitsPercentage.toFixed(2)}%
											</div>
										</div>
									{/each}

									<ul
										class="Box-row d-flex flex-row flex-justify-between rounded-0"
										style="background: #b4f1b4 !important; color: #22272e !important;">
										<li class="h4 col-3 text-bold">Sub-Total</li>

										<li class="col-1 text-bold">
											{combat.procsAndTicks.filter((e) => e.type === 'unknown').reduce((acc, el) => acc + el.hits, 0)}
										</li>

										<li class="col-1 text-bold">
											{combat.procsAndTicks
												.filter((e) => e.type === 'unknown')
												.reduce((acc, el) => acc + el.heals, 0)
												.toLocaleString(undefined, {
													minimumFractionDigits: 2,
													maximumFractionDigits: 2,
												})}
										</li>
										<li class="col-1 text-bold">
											{combat.procsAndTicks
												.filter((e) => e.type === 'unknown')
												.reduce((acc, el) => acc + el.hps, 0)
												.toLocaleString(undefined, {
													minimumFractionDigits: 2,
													maximumFractionDigits: 2,
												})}
										</li>

										<li class="col-1 text-bold">
											{combat.procsAndTicks
												.filter((e) => e.type === 'unknown')
												.reduce((acc, el) => acc + el.healPercentage, 0)
												.toFixed(2)}%
										</li>
										<li class="col-1 text-bold">
											{#if combat.procsAndTicks.filter((e) => e.type === 'unknown').length <= 0}
												0.00%
											{:else}
												{(
													(combat.procsAndTicks
														.filter((e) => e.type === 'unknown')
														.reduce((acc, el) => acc + el.criticalHits, 0) /
														combat.procsAndTicks
															.filter((e) => e.type === 'unknown')
															.reduce((acc, el) => acc + el.hits, 0)) *
													100
												).toFixed(2)}%
											{/if}
										</li>
									</ul>

									<ul
										class="Box-row d-flex flex-row flex-justify-between rounded-bottom-3"
										style="background: #245829 !important; color: #cdd9e5 !important">
										<li class="h4 col-3 text-bold">Total</li>

										<li class="col-1 text-bold">
											{combat.procsAndTicks
												.concat(combat.abilityTrigger)
												.filter((e) => e.type === 'unknown')
												.reduce((acc, el) => acc + el.hits, 0)}
										</li>

										<li class="col-1 text-bold">
											{combat.procsAndTicks
												.concat(combat.abilityTrigger)
												.filter((e) => e.type === 'unknown')
												.reduce((acc, el) => acc + el.heals, 0)
												.toLocaleString(undefined, {
													minimumFractionDigits: 2,
													maximumFractionDigits: 2,
												})}
										</li>
										<li class="col-1 text-bold">
											{combat.procsAndTicks
												.concat(combat.abilityTrigger)
												.filter((e) => e.type === 'unknown')
												.reduce((acc, el) => acc + el.hps, 0)
												.toLocaleString(undefined, {
													minimumFractionDigits: 2,
													maximumFractionDigits: 2,
												})}
										</li>

										<li class="col-1 text-bold">
											{combat.procsAndTicks
												.concat(combat.abilityTrigger)
												.filter((e) => e.type === 'unknown')
												.reduce((acc, el) => acc + el.healPercentage, 0)
												.toFixed(2)}%
										</li>
										<li class="col-1 text-bold">
											{#if combat.procsAndTicks
												.concat(combat.abilityTrigger)
												.filter((e) => e.type === 'unknown').length <= 0}
												0.00%
											{:else}
												{(
													(combat.procsAndTicks
														.concat(combat.abilityTrigger)
														.filter((e) => e.type === 'unknown')
														.reduce((acc, el) => acc + el.criticalHits, 0) /
														combat.procsAndTicks
															.concat(combat.abilityTrigger)
															.filter((e) => e.type === 'unknown')
															.reduce((acc, el) => acc + el.hits, 0)) *
													100
												).toFixed(2)}%
											{/if}
										</li>
									</ul>
								</div>
							{/if}
						</div>
					{:else if !noFileSelected}
						<div class="blankslate blankslate-narrow">
							<h3 class="mb-1 h1">Trace your combats!</h3>
							<p>
								Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed eu urna laoreet, euismod mi vel, porttitor
								magna.
							</p>

							<form>
								<select
									class="form-select"
									aria-label="Combat logs"
									disabled={realTimeParse}
									bind:value={selectedFile}
									on:blur={selectCombat}
									on:change={selectCombat}>
									<option values="">Select a combat log</option>

									{#each files as file}
										<option value={file}>{interpretCombatFileName(file)}</option>
									{/each}
								</select>
							</form>
						</div>
					{/if}
				</div>
			</div>
		</div>
	</div>
{:else if selectedMenu === 'settings'}
	<Settings />
{:else}
	<div class="Layout Layout--sidebarPosition-flowRow-end Layout--gutter-none">
		<div class="Layout-main">
			<div class="blankslate blankslate-narrow">
				<img src="logo.svg" />
				<h3 class="mb-1 h1">Welcome to Trace Parse!</h3>
				<p>
					Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed eu urna laoreet, euismod mi vel, porttitor magna.
					Nam accumsan, urna sed pellentesque tempor, leo nunc iaculis nibh, non lacinia lectus quam nec eros. Phasellus
					a congue quam. Sed malesuada quam non odio fringilla mollis.
				</p>

				<button class="btn btn-outline my-3" type="button" aria-selected={realTimeParse} on:click={toggleParse}
					>Parse</button>
			</div>
		</div>
	</div>
{/if}
