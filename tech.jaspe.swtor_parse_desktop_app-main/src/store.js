import { writable } from 'svelte/store';

const personalStats = writable({
	apm: 0,
	gcd: 1.5,
	dps: 0,
	damage: 0,
	hps: 0,
	threat: 0,
});

export { personalStats };
