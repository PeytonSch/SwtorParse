const moment = require('moment-timezone');
const { median, mean } = require('mathjs');
const hash = require('object-hash');
const { v4: uuidv4 } = require('uuid');

moment.defaultFormat = "YYYY-MM-DD HH:mm:ss.SS";
const MOMENT_FORMAT = "YYYY-MM-DD HH:mm:ss.SS";

//// Actions ID
const SafeLoginImmunity = '973870949466372';
const EnterCombat = '836045448945489';
const ExitCombat = '836045448945490';
const EndCombat = '4196741393940480';
const Event = '836045448945472';
const AbilityActivated = '836045448945479';
const Damage = '836045448945501';
const Absorbed = '836045448945511';
const Heal = '836045448945500';
const ApplyEffect = '836045448945477';
const RemoveEffect = '836045448945478';
const Death = '836045448945493';
const OperationDebuff = '3322127138685184';

const Dummy10MHealth = '4185080557731840';
const Dummy6_5MHealth = '4185076262764544';
const Dummy3_250MHealth = '4185071967797248';
const Dummy1MHealth = '4185054787928064';

//Stealth abilities
const StealthAssassin = '808317140074496';
const ForceCloakAssassin = '2271329029980160';

const StealthShadow = '812852625539072';
const ForceCloakShadow = '2271612497821696';

const StealthOperative = '814901324939264';
const CloakingScreenOperative = '2278037768896512';

const StealthScoundrel = '807269168054272';
const DisappearingActScoundrel = '2276212407795712';

//Revive abilities
const OnboardAED = '2940854301884416';
const ResuscitationProbe = '814875555135488';
const Reanimation = '808287075303424';

let history = [];

////
// Gets the date from the combat file name. Returns a moment
// object
////
function getDateFromCombatFileName(fileName) {
  if (!fileName.includes('combat_')) {
    return moment();
  }

  const name = fileName.replace('combat_', '').replace('.txt', '').trim();
  const date = name.substring(0, name.indexOf('_')).trim();
  const time = name.substring(name.indexOf('_') + 1, name.length).trim();
  return moment(
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
}

////
// Deserialize a combat log line into a json object. Post message
// to parse worker.
////
function convertLineToJson(fileName, lines) {
  const date = getDateFromCombatFileName(fileName);
  let previousTime;

  for (let line of lines) {
    let startFrom = 1;

    //Timestamp
    const timestamp = line.substring(startFrom, line.indexOf(']', startFrom));
    startFrom += timestamp.length + 1;

    //Source
    const source = line.substring(line.indexOf('[', startFrom) + 1, line.indexOf(']', startFrom));
    startFrom += source.length + 3;

    //Target
    const target = line.substring(line.indexOf('[', startFrom) + 1, line.indexOf(']', startFrom));
    startFrom += target.length + 3;

    //Ability
    const ability = line.substring(line.indexOf('[', startFrom) + 1, line.indexOf(']', startFrom));
    startFrom += ability.length + 3;

    //Event
    const event = line.substring(line.indexOf('[', startFrom) + 1, line.indexOf(']', startFrom));
    startFrom += event.length + 3;

    //Value
    const value = line.substring(line.indexOf('(', startFrom), line.indexOf(')', startFrom) + 1);
    startFrom += value.length + 3;

    //Threat
    const threat = line.substring(line.indexOf('<') + 1, line.indexOf('>'));

    const currentTime = moment(`${date.format('YYYY-MM-DD')} ${timestamp}`);

    if (typeof previousTime !== 'undefined' && previousTime.hour() >= 12 && previousTime.hour() <= 23 && currentTime.hour() >= 0 && currentTime.hour() <= 11) {
      date.add(1, 'day');
    }

    previousTime = moment(`${date.format('YYYY-MM-DD')} ${timestamp}`);

    const log = {
      id: uuidv4(),
      timestamp: `${date.format('YYYY-MM-DD')} ${timestamp}`,
      source: source,
      target: target,
      abilityId: ability.substring(ability.indexOf('{') + 1, ability.indexOf('}')),
      ability: ability,
      actions: event.split(':').map((e) => e.trim()),
      value: value,
      damage: null,
      heal: null,
      threat: !isNaN(threat) && !isNaN(parseInt(threat)) ? parseInt(threat) : 0,
      line: line
    };

    if (typeOfTrigger(log, Heal)) {
      log.heal = {
        value: 0,
        critical: false
      };

      if (value.includes('*')) {
        log.heal.value = parseInt(value.substring(1, value.indexOf('*')));
        log.heal.critical = true;
      } else {
        log.heal.value = parseInt(value.substring(1, value.indexOf(')')));
      }
    } else if (typeOfTrigger(log, Damage)) {
      log.damage = {
        value: 0,
        absorbed: 0,
        critical: false,
        type: 'unknown',
      };

      if (value.includes(Absorbed)) {
        const absorbed = value.substring(value.indexOf('(', 1) + 1, value.indexOf(')', 1))
        log.damage.absorbed = parseInt(absorbed.substring(0, absorbed.indexOf(' ')))
      }

      if (value.includes('*')) {
        log.damage.value = parseInt(value.substring(1, value.indexOf('*')));
        log.damage.critical = true;
      } else {
        log.damage.value = parseInt(value.substring(1, value.indexOf(' ')));
      }

      log.damage.type = value.substring(value.indexOf(' ') + 1, value.lastIndexOf(' '));
    }

    history.push(log);
  }
}

////
// Verify if the given log, includes the given actionId
////
function typeOfTrigger(log, actionId) {
  return log.actions.some(e => e.includes(actionId));
}

////
// Sage a log to the logs array
////
function saveToLogs(combat, log, precast) {
  combat.logs.push({
    ...log,
    precast: precast,
  });
}

////
// Verify if the player stealth out or was revive
////
function playerStealthOutOrRevive(combat, log, secondsBack) {
  if (typeof combat === 'undefined') return false;

  let value = false;

  const timestampSecondBack = moment(log.timestamp, MOMENT_FORMAT).subtract(secondsBack, 'seconds');
  const backlogs = history.filter((e) => moment(e.timestamp, MOMENT_FORMAT) >= timestampSecondBack &&
    moment(e.timestamp, MOMENT_FORMAT) < moment(log.timestamp, MOMENT_FORMAT));

  for (let i = backlogs.length - 1; i >= 0; i--) {
    const backlog = backlogs[i];

    if (typeOfTrigger(backlog, RemoveEffect) && typeOfTrigger(backlog, OperationDebuff)) {
      value = false;
      break;
    }

    if (typeOfTrigger(backlog, Event) && typeOfTrigger(backlog, AbilityActivated) && backlog.abilityId === EndCombat) {
      value = false;
      break;
    }

    if (typeOfTrigger(backlog, ExitCombat)) {
      break;
    }

    if (typeOfTrigger(backlog, Event) && typeOfTrigger(backlog, AbilityActivated) && (
      backlog.abilityId === ForceCloakAssassin ||
      backlog.abilityId === ForceCloakShadow ||
      backlog.abilityId === CloakingScreenOperative ||
      backlog.abilityId === DisappearingActScoundrel
    )) {
      combat.abilityTrigger.push({
        ...backlog,
        precast: false,
      });

      value = true;
      saveToLogs(combat, backlog, false);
      break;
    }

    if (typeOfTrigger(backlog, ApplyEffect) && log.target === combat.source && log.source !== combat.source && (
      backlog.abilityId === OnboardAED ||
      backlog.abilityId === ResuscitationProbe ||
      backlog.abilityId === Reanimation
    )) {
      value = true;
      saveToLogs(combat, backlog, false);
      break;
    }
  }

  return value;
}

////
// Verify if the dummy health was apply
///
function applyDummyHealth(log) {
  let value = null;

  const timestampSecondBack = moment(log.timestamp, MOMENT_FORMAT).subtract(60, 'seconds');
  const backlogs = history.filter((e) => moment(e.timestamp, MOMENT_FORMAT) >= timestampSecondBack &&
    moment(e.timestamp, MOMENT_FORMAT) < moment(log.timestamp, MOMENT_FORMAT));

  for (let i = backlogs.length - 1; i >= 0; i--) {
    const backlog = backlogs[i];

    if (typeOfTrigger(backlog, ExitCombat)) {
      break;
    }

    if (typeOfTrigger(backlog, Event) && typeOfTrigger(backlog, AbilityActivated)) {
      if (backlog.abilityId === Dummy10MHealth) {
        value = 10000000
      } else if (backlog.abilityId === Dummy6_5MHealth) {
        value = 6500000
      } else if (backlog.abilityId === Dummy3_250MHealth) {
        value = 3250000
      } else if (backlog.abilityId === Dummy1MHealth) {
        value = 1000000
      }
    }
  }

  return value;
}

////
// Get the abilities that were execute 6 seconds before entering combat
////
function getPrecastAbilities(combat, log) {
  const timestamp6SecondBack = moment(log.timestamp, MOMENT_FORMAT).subtract(6, 'seconds');
  const backlog = history.filter((e) => moment(e.timestamp, MOMENT_FORMAT) >= timestamp6SecondBack &&
    moment(e.timestamp, MOMENT_FORMAT) < moment(log.timestamp, MOMENT_FORMAT));

  for (let i = backlog.length - 1; i >= 0; i--) {
    if (typeOfTrigger(backlog[i], ExitCombat)) {
      break;
    }

    parseAbilityByType(combat, backlog[i], true);
  }
}

////
// Get the abilities that were record 500ms after combat ended
///
function getPostCastAbilities(combat, log) {
  const timestamp6SecondsForward = moment(log.timestamp, MOMENT_FORMAT).add(6, 'seconds');

  const postLogs = history.filter(
    (e) => moment(e.timestamp, MOMENT_FORMAT) <= timestamp6SecondsForward && moment(e.timestamp, MOMENT_FORMAT) > moment(log.timestamp, MOMENT_FORMAT)
  );

  for (let i = 0; i < postLogs.length; i++) {
    if (typeOfTrigger(postLogs[i], EnterCombat)) {
      break;
    }

    parseAbilityByType(combat, postLogs[i], false, true);
  }
}

////
// Verify if the ability was a trigger event. Was an apply effect or was an ability taken.
// Ability trigger: Are the powers (abilities) that are trigger when a player click or press the keyboard shortcut for that cast.
// Apply effect: Are the execution of an ability trigger, a tick, proc, damage or heal receive.
////
function parseAbilityByType(combat, log, precast = false, postCast = false) {
  if (typeof combat.logs.find(e => e.id === log.id) !== 'undefined') return;

  if (typeOfTrigger(log, Event) && typeOfTrigger(log, AbilityActivated) && log.source === combat.source && !postCast) {
    saveToLogs(combat, log, precast);

    combat.abilityTrigger.push({
      ...log,
      precast: precast,
    });
  }

  if ((typeOfTrigger(log, Damage) || typeOfTrigger(log, Heal)) && typeOfTrigger(log, ApplyEffect) && !precast) {
    saveToLogs(combat, log, precast);

    if (log.source !== combat.source) {
      combat.abilitiesTaken.push(log);
    } else {
      combat.abilitiesApply.push({
        ...log,
        precast: precast,
      });
    }
  }
}

////
// Generate the statistical information base on a given combat
///
function generateStats(combat, realtime) {
  try {
    if (typeof combat === 'undefined') return;
    if (combat.logs.length <= 0) return;
    if (combat.abilityTrigger.length <= 0) return;
    if (combat.abilitiesApply.length <= 0) return;

    combat.logs = combat.logs.sort((a, b) => moment(a.timestamp, MOMENT_FORMAT).unix() - moment(b.timestamp, MOMENT_FORMAT).unix());
    combat.abilityTrigger = combat.abilityTrigger.sort((a, b) => moment(a.timestamp, MOMENT_FORMAT).unix() - moment(b.timestamp, MOMENT_FORMAT).unix());
    combat.abilitiesApply = combat.abilitiesApply.sort((a, b) => moment(a.timestamp, MOMENT_FORMAT).unix() - moment(b.timestamp, MOMENT_FORMAT).unix());
    combat.abilitiesTaken = combat.abilitiesTaken.sort((a, b) => moment(a.timestamp, MOMENT_FORMAT).unix() - moment(b.timestamp, MOMENT_FORMAT).unix());

    const lastExitCombatLogs = combat.logs.filter(log => typeOfTrigger(log, ExitCombat) || (typeOfTrigger(log, Death) && log.target === combat.source && log.source !== combat.source))

    const lastExitCombatTimestamp = lastExitCombatLogs.length > 0 ? lastExitCombatLogs[lastExitCombatLogs.length - 1].timestamp : combat.logs[combat.logs.length - 1].timestamp;
    const lasAbilityApply = combat.abilitiesApply[combat.abilitiesApply.length - 1];
    const lasAbilityApplyTimestamp = typeof lasAbilityApply !== 'undefined' ? lasAbilityApply.timestamp : combat.logs[combat.logs.length - 1].timestamp;

    const start = combat.timestamp;
    const end = moment(lastExitCombatTimestamp, MOMENT_FORMAT)
      .isAfter(moment(lasAbilityApplyTimestamp, MOMENT_FORMAT))
      ? lastExitCombatTimestamp
      : lasAbilityApplyTimestamp;
    const duration = moment(end, MOMENT_FORMAT).diff(moment(start, MOMENT_FORMAT), 'seconds', true);

    //This is how GCD should work?
    const GCDs = combat.abilityTrigger
      .filter(
        (e) =>
          combat.abilitiesApply.some(
            (e2) => e.abilityId === e2.abilityId && (e2.damage !== null || e2.heal !== null)
          ) && e.precast === false
      )
      .reduce((acc, el) => {
        return [
          ...acc,
          {
            GCD:
              acc.length <= 0
                ? moment(el.timestamp, MOMENT_FORMAT).diff(moment(start, MOMENT_FORMAT), 'seconds', true)
                : moment(el.timestamp, MOMENT_FORMAT).diff(moment(acc[acc.length - 1].timestamp, MOMENT_FORMAT), 'seconds', true),
            timestamp: el.timestamp,
            abilityId: el.abilityId,
            ability: el.ability.replace(el.abilityId, '').replace(/[{}]/g, '').trim(),
          },
        ];
      }, []);

    const damage = combat.abilitiesApply.filter((e) => e.damage !== null).reduce((acc, el) => acc + el.damage.value, 0);
    const damageTaken = combat.abilitiesTaken.filter((e) => e.damage !== null).reduce((acc, el) => acc + el.damage.value, 0);
    const damageAbsorbed = combat.abilitiesTaken.filter((e) => e.damage !== null).reduce((acc, el) => acc + el.damage.absorbed, 0);

    const heals = combat.abilitiesApply.filter((e) => e.heal !== null).reduce((acc, el) => acc + el.heal.value, 0);
    const healsTaken = combat.abilitiesTaken.filter((e) => e.heal !== null).reduce((acc, el) => acc + el.heal.value, 0);

    const threat = combat.abilitiesApply.reduce((acc, el) => acc + el.threat, 0);

    const hits = combat.abilitiesApply.filter((e) => e.damage !== null || e.heal !== null).length;
    const critHits = combat.abilitiesApply.filter((e) => (e.damage !== null && e.damage.critical === true) || (e.heal !== null && e.heal.critical === true)).length;

    const stat = {
      //////// Basic information ////////
      battle: combat.battle,
      player: combat.source,
      health: combat.health,
      targets: [
        ...new Set(
          combat.logs
            .filter((e) => combat.source !== e.target && e.target.trim().length > 0 && e.source.trim().length > 0)
            .filter(e => e.target !== "" && typeof e.target !== 'undefined')
            .map((e) => e.target)
        ),
      ],
      start: start,
      end: end,

      //////// Combat summary ////////
      duration: duration,
      apm: duration === 0 ? 0 : combat.abilityTrigger.filter((e) => e.precast === false).length / (duration / 60),

      // Hits
      hits: hits,
      normalHits: combat.abilitiesApply.filter((e) => (e.damage !== null && e.damage.critical === false) || (e.heal !== null && e.heal.critical === false)).length,
      criticalHits: critHits,
      criticalHitsPercentage: hits === 0 ? 0 : (critHits / hits) * 100,

      // Per second
      dps: duration === 0 ? 0 : damage / duration,
      hps: duration === 0 ? 0 : heals / duration,
      dtps: duration === 0 ? 0 : damageTaken / duration,
      htps: duration === 0 ? 0 : healsTaken / duration,
      dabps: duration === 0 ? 0 : damageAbsorbed / duration,
      tps: duration === 0 ? 0 : threat / duration,

      // Totals
      damage: damage,
      heals: heals,
      damageTaken: damageTaken,
      healsTaken: healsTaken,
      damageAbsorbed: damageAbsorbed,
      threat: threat,

      //////// GCDs stats ////////
      gcdMean: GCDs.length <= 0 ? 0 : mean(GCDs.map((e) => e.GCD)),
      gcdMedian: GCDs.length <= 0 ? 0 : median(GCDs.map((e) => e.GCD)),
      gcdMin: GCDs.length <= 0 ? 0 : Math.min(...GCDs.map((e) => e.GCD)),
      gcdMax: GCDs.length <= 0 ? 0 : Math.max(...GCDs.map((e) => e.GCD)),
      gcds: GCDs,

      //////// Abilities detail ////////
      abilitiesTaken: combat.abilitiesTaken,
      abilitiesApply: combat.abilitiesApply,
      abilityTrigger: combat.abilityTrigger.reduce((abilitiesAcc, el) => {
        if (!combat.abilitiesApply.some((e) => e.abilityId === el.abilityId)) return abilitiesAcc;

        const el2 = abilitiesAcc.find((e) => e.abilityId === el.abilityId);

        if (typeof el2 === 'undefined') {
          return [...abilitiesAcc, getAbilityStats(damage, heals, duration, combat.abilitiesApply, el)];
        }

        return abilitiesAcc;
      }, []),
      procsAndTicks: combat.abilitiesApply.reduce((procsAndTicks, el) => {
        if (combat.abilityTrigger.some((e) => e.abilityId === el.abilityId)) return procsAndTicks;

        const el2 = procsAndTicks.find((e) => e.abilityId === el.abilityId);

        if (typeof el2 === 'undefined') {
          return [...procsAndTicks, getAbilityStats(damage, heals, duration, combat.abilitiesApply, el)];
        }

        return procsAndTicks;
      }, []),
      logs: combat.logs
    };

    const id = hash(stat);
    stat['id'] = id;
    return stat;
  } catch (err) {
    process.send(JSON.stringify({
      type: 'error',
      data: `Error on parse child process, generating stat: ${err}`
    }));
    return;
  }
}

////
// Generate the statistical information base on a specific ability
////
function getAbilityStats(damage, heals, duration, abilitiesApply, el) {
  const abilityCritHit = abilitiesApply.filter(
    (e) =>
      ((e.damage !== null && e.damage.critical === true) || (e.heal !== null && e.heal.critical === true)) &&
      e.abilityId === el.abilityId
  ).length;
  const abilityHit = abilitiesApply.filter(
    (e) => (e.damage !== null || e.heal !== null) && e.abilityId === el.abilityId
  ).length;
  const abilityDmg = abilitiesApply
    .filter((e) => e.abilityId === el.abilityId && e.damage !== null)
    .reduce((acc, el) => (acc += el.damage.value), 0);
  const abilityHeals = abilitiesApply
    .filter((e) => e.abilityId === el.abilityId && e.heal !== null)
    .reduce((acc, el) => (acc += el.heal.value), 0);
  const missHits = abilitiesApply.filter(
    (e) =>
      e.abilityId === el.abilityId &&
      e.damage !== null &&
      (e.damage.type === '-miss' || e.damage.type === '-dodge' || e.damage.type === '-parry')
  ).length;
  const type = abilitiesApply.find(
    (e) =>
      e.abilityId === el.abilityId &&
      e.damage !== null &&
      e.damage.type !== '-resist' &&
      e.damage.type !== '-miss' &&
      e.damage.type !== '-dodge' &&
      e.damage.type !== '-parry'
  );

  return {
    abilityId: el.abilityId,
    ability: el.ability.replace(el.abilityId, '').replace(/[{}]/g, '').trim(),
    precast: el.precast,
    type: typeof type !== 'undefined' ? type.damage.type.includes(' ') ? type.damage.type.substring(0, type.damage.type.indexOf(" ")) : type.damage.type : 'unknown',
    damagePercentage: damage === 0 ? 0 : (abilityDmg / damage) * 100,
    healPercentage: heals === 0 ? 0 : (abilityHeals / heals) * 100,
    hits: abilityHit,
    damage: abilityDmg,
    heals: abilityHeals,
    dps: duration === 0 ? 0 : abilityDmg / duration,
    hps: duration === 0 ? 0 : abilityHeals / duration,
    normalHits: abilitiesApply.filter(
      (e) =>
        ((e.damage !== null && e.damage.critical === false) || (e.heal !== null && e.heal.critical === false)) &&
        e.abilityId === el.abilityId
    ).length,
    criticalHits: abilityCritHit,
    criticalHitsPercentage: abilityHit === 0 ? 0 : (abilityCritHit / abilityHit) * 100,
    missHits: missHits,
    missHitsPercentage: abilityHit === 0 ? 0 : (missHits / abilityHit) * 100,
    threat: abilitiesApply.filter((e) => e.abilityId === el.abilityId).reduce((acc, el) => acc + el.threat, 0),
    tps: duration === 0 ? 0 : abilitiesApply.filter((e) => e.abilityId === el.abilityId).reduce((acc, el) => acc + el.threat, 0) / duration,
  };
}

////
// Generate all the combats present in the params logs
////
function parse(logs, realtime) {
  let combats = [];
  let combatIndex = 0;
  let inCombat = false;

  for (let i = 0; i < logs.length; i++) {
    const log = logs[i];

    if (typeOfTrigger(log, EnterCombat)) {
      inCombat = true;

      const health = applyDummyHealth(log);

      if (health !== null || !playerStealthOutOrRevive(combats[combatIndex], log, 60 * 2)) {
        combats.push({
          internalId: uuidv4(),
          health: health,
          timestamp: log.timestamp,
          source: log.source,
          battle: log.value,
          abilityTrigger: [],
          abilitiesApply: [],
          abilitiesTaken: [],
          logs: [],
        });
        combatIndex = combats.length - 1;
        getPrecastAbilities(combats[combatIndex], log);
        saveToLogs(combats[combatIndex], log);
      }
    } else if (typeOfTrigger(log, ExitCombat) || (typeOfTrigger(log, Death) && log.target === combats[combatIndex].source && log.source !== combats[combatIndex].source)) {
      inCombat = false;
      saveToLogs(combats[combatIndex], log);
      getPostCastAbilities(combats[combatIndex], log);
    } else if (inCombat) {
      parseAbilityByType(combats[combatIndex], log, false);
    }

    process.send(JSON.stringify({
      type: 'parse_log_percentage',
      data: ((i + 1) / logs.length) * 100
    }));
  }

  return combats.map(e => generateStats(e, realtime)).filter(e => typeof e !== 'undefined' && e !== null);

}

function getCurrentCombat() {
  const enterCombats = history.filter(log => typeOfTrigger(log, EnterCombat));

  // process.send(JSON.stringify({
  //   type: 'info',
  //   data: enterCombats
  // }))

  if (enterCombats.length <= 1) {
    process.send(JSON.stringify({
      type: 'current_combat',
      data: parse(history, true)
    }));
    return;
  }

  process.send(JSON.stringify({
    type: 'current_combat',
    data: parse(history.slice(history.indexOf(enterCombats[enterCombats.length - 1]), history.length), true)
  }));
}

function getCombats(fileName) {
  const stats = parse([...history], false);
  process.send(JSON.stringify({
    type: 'parse_logs',
    data: {
      file: fileName,
      stats: stats
    }
  }));
}

process.on('message', (msg) => {
  const message = JSON.parse(msg);

  try {
    switch (message.type) {
      case 'parse_logs': {
        history = [];
        convertLineToJson(message.data.fileName, message.data.lines);
        getCombats(message.data.fileName);
      } break;

      case 'parse_full_file': {
        history = [];
        convertLineToJson(message.data.fileName, message.data.lines);
        process.send(JSON.stringify({
          type: 'ready_current_combat'
        }));
      } break;

      case 'parse_line': {
        convertLineToJson(message.data.fileName, message.data.lines);
      } break;

      case 'current_combat':
        getCurrentCombat();
        break;
    }
  } catch (err) {
    process.send(JSON.stringify({
      type: 'error',
      data: `Error on parse child process: ${err}`
    }));
  }
});

