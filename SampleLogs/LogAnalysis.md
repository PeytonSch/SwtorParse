Analysis of New Combat Log Format
---

#### Affects Applied to Oneself
```
[21:57:07.879] [@Heavy Sloth#689203382607232|(-388.22,19.78,94.98,-21.95)|(44307/46271)] [=] [Unnatural Might {4503002626916352}] [ApplyEffect {836045448945477}: Unnatural Might {4503002626916634}]
```

#### Companion Action
> Companion actions are signified by the Performer being a player tag and then `/Companion`
```
[21:57:10.048] [@Heavy Sloth#689203382607232/Arcann {3915326546771968}:28040000128828|(-390.07,19.03,94.98,-111.95)|(42695/42695)] [@Heavy Sloth#689203382607232|(-388.22,19.78,94.98,-21.95)|(46271/46271)] [Mending {3590163162726400}] [ApplyEffect {836045448945477}: Heal {836045448945500}] (1967* ~1963) <392>
```


Misc Logged Information
---
#### Spec Changes: 
> This appears to happen on login as well, so you have access to initial class
```
[21:57:07.750] [@Heavy Sloth#689203382607232|(-388.22,19.78,94.98,-21.95)|(44307/44307)] [] [] [DisciplineChanged {836045448953665}: Juggernaut {16141180228828243745}/Vengeance {2031339142381576}]
```

#### Area Entered:

```
[21:57:07.750] [@Heavy Sloth#689203382607232|(-388.22,19.78,94.98,-21.95)|(44307/44307)] [] [] [AreaEntered {836045448953664}: Oricon {137438993357}] (HE600) <v7.0.0b>
```

#### Stun Durations
```
[22:08:22.933] [@Heavy Sloth#689203382607232|(76.22,-319.16,-21.46,-90.15)|(2909/2909)] [Rival Acolyte {287749923930112}:26518005423499|(71.83,-322.10,-21.27,147.59)|(0/240)] [Vengeful Slam {3503361873674240}] [ApplyEffect {836045448945477}: Stunned (Physical) {3503361873674516}]
[22:08:22.938] [@Heavy Sloth#689203382607232|(76.22,-319.16,-21.46,-90.15)|(2909/2909)] [Competing Acolyte {379408820994048}:26518005423543|(76.87,-319.32,-21.46,92.86)|(0/240)] [Vengeful Slam {3503361873674240}] [RemoveEffect {836045448945478}: Stunned (Physical) {3503361873674516}]
```

#### Target Set / Cleared
> Could use this information to see things like amount of time you have aggro, or how long you had aggro before you died.
> You could also use this for how long you do not have aggro.
```
[22:08:23.696] [@Igrin#689797178977446/Vette {290296839536640}:26518005402831|(56.93,-321.12,-21.02,-99.62)|(2100/2100)] [@Igrin#689797178977446|(70.62,-318.79,-21.37,-91.55)|(1999/1999)] [] [Event {836045448945472}: TargetSet {836045448953668}]
[22:08:23.097] [@Heavy Sloth#689203382607232|(76.22,-319.16,-21.46,-90.15)|(2909/2909)] [] [] [Event {836045448945472}: TargetCleared {836045448953669}]
[22:08:22.790] [Acolyte Henchman {379421705895936}:26518005423454|(71.85,-316.66,-21.30,43.72)|(130/130)] [@Igrin#689797178977446/Vette {290296839536640}:26518005402831|(56.93,-321.12,-21.02,-99.62)|(2100/2100)] [] [Event {836045448945472}: TargetSet {836045448953668}]
```

#### With Enter Combat and Target Set we may be able to create an overlay of all the things you are in combat with and their health and targets and distance from you!
```
[20:59:40.780] [@Heavy Sloth#689203382607232|(-636.82,223.70,13.10,-82.13)|(46271/46271)] [] [] [Event {836045448945472}: EnterCombat {836045448945489}]
[20:59:40.807] [Dread Host Soldier {3266932513964032}:28040000012626|(-609.69,223.44,11.28,450.00)|(6618/7950)] [@Heavy Sloth#689203382607232|(-636.82,223.70,13.10,-82.13)|(46271/46271)] [] [Event {836045448945472}: TargetSet {836045448953668}]
[20:59:40.809] [Dread Host Slaver {3266941103898624}:28040000035026|(-602.23,217.40,12.70,225.00)|(6740/6740)] [@Heavy Sloth#689203382607232|(-636.82,223.70,13.10,-82.13)|(46271/46271)] [] [Event {836045448945472}: TargetSet {836045448953668}]
[20:59:40.809] [Dread Host Soldier {3266932513964032}:28040000034858|(-611.39,224.86,11.22,450.00)|(7950/7950)] [@Heavy Sloth#689203382607232|(-636.82,223.70,13.10,-82.13)|(46271/46271)] [] [Event {836045448945472}: TargetSet {836045448953668}]
[20:59:40.809] [Dread Host Soldier {3266932513964032}:28040000023320|(-603.21,227.34,12.82,450.00)|(7950/7950)] [@Heavy Sloth#689203382607232|(-636.82,223.70,13.10,-82.13)|(46271/46271)] [] [Event {836045448945472}: TargetSet {836045448953668}]
```

#### Lets you know when you kill something
```
[22:08:49.010] [@Igrin#689797178977446|(131.38,-323.90,-22.45,167.92)|(1999/1999)] [Rival Acolyte {287749923930112}:26518003902330|(131.15,-323.95,-22.44,114.22)|(0/240)] [] [Event {836045448945472}: Death {836045448945493}]
```

Observations
---
Every single line begins with this format:
> [timestamp] [Performer | (Position) | (Health)]
```
[21:57:07.880] [@Heavy Sloth#689203382607232|(-388.22,19.78,94.98,-21.95)|(44307/46271)]
```
> This can be followed with [] or [=] or [Target] it seems... Some examples
```
[@Heavy Sloth#689203382607232|(4647.56,4698.56,710.01,-101.01)|(1/360708)] [] [] [AreaEntered

[@Heavy Sloth#689203382607232|(4647.56,4698.56,710.01,-101.01)|(360708/373436)] [=] [Unnatural Might

[@Heavy Sloth#689203382607232/Arcann {3915326546771968}:23599041554636|(4647.79,4697.38,708.41,168.99)|(282197/282197)] [=] [Mending {3590163162726400}] 

[@Heavy Sloth#689203382607232/Arcann {3915326546771968}:23599041554636|(4647.79,4697.38,708.41,168.99)|(282197/282197)] [@Heavy Sloth#689203382607232|

// This one has no ability name
[@Heavy Sloth#689203382607232/Arcann {3915326546771968}:23599041554636|(4647.79,4697.38,710.01,168.99)|(282197/295231)] [=] [ {4196681264398336}] [ApplyEffect {836045448945477}: Coordination {4196681264398637}]

```

IDs for Mobs are broken into `Name {MobTypeID}:IndividualMobID`. So for example:
`Rival Acolyte {287749923930112}:26518003904775`
All Rival Acolytes in the game have the same `Rival Acolyte {287749923930112}` part


This pattern recognizes performers and targets that are players:
```
\[@*\w*\s*\w*#\d*\|\(-*\d*.\d\d,-*\d*.\d\d,-*\d*.\d*,-*\d*.\d\d\)\|\(\d*/\d*\)\]
```
This would match these examples:
```
[@Heavy Sloth#689203382607232|(-65.44,-57.60,-0.14,-83.93)|(2909/2909)]
[@Igrin#689797178977446|(2.02,-123.54,-11.44,1.28)|(1999/1999)]

```
This does not match Mobs or companions, this is because companions have the `/companion` and mobs have a different ID setup


Possible Implementation Ideas
---
* Do some matching to figure out what type it might be
    * Top level matching might look at is it a player, companion, or mob action
    * Is it combat entered?
        * Then look at target maybe?
* Try having extractor functions that look for specific information to reuse patterns
    * So can always have an extract timestamp and performer for example
    
    Something like:
    ```
  line match {
    case combat_line => {
        timestamp = extract_timestamp(line)
          ....
  }
  ...
  }
    ```
* Maybe have a line struct with default null values and then just extract the values that are present in each line?
    * Struct could have a type field as well to signify meta data
    
    
    
    
    
    
Notes on how values are represented:
- Most values will have `(value type {id})`, the lines that dont seem to mostly be from companions
    - For example `(1332 energy {836045448940874})` signifies 1332 energy damage with that id
- An `*` represents a critical value
- `<###>` represents the amount of threat from that action. This is optional and is not always present
* Some Types of values, Question: what does `~` signify? (Thinking it may be the effective amount due to an overheal or over dps):
    * `(1332 energy {836045448940874}) <1332>`
    * `(57* energy {836045448940874}) <40>`
    * `(6502* ~4879 energy {836045448940874}) <6502>`
    * `(0 -deflect {836045448945508}) <1>`
    * `(1764 ~99)`
    * `(1274 ~0)`
    * `(0 -miss {836045448945502}) <1>`
    * `(3054 energy {836045448940874} -shield {836045448945509} (1562 absorbed {836045448945511})) <3054>`
    * `(511 ~73 energy {836045448940874} -shield {836045448945509} (272 absorbed {836045448945511})) <511>`
    
Some complete log lines with a `~`:
```
[22:08:42.317] [@Heavy Sloth#689203382607232/Arcann {3915326546771968}:26518005427235|(129.14,-323.03,-22.15,-76.47)|(2944/2944)] [=] [Self Preservation {4238737584160768}] [ApplyEffect {836045448945477}: Heal {836045448945500}] (88 ~85)
[22:08:43.717] [@Igrin#689797178977446|(131.38,-323.90,-22.45,-116.33)|(1999/1999)] [Rival Acolyte {287749923930112}:26518003902286|(131.10,-323.50,-22.45,103.53)|(0/345)] [Retaliation {1582223002173440}] [ApplyEffect {836045448945477}: Damage {836045448945501}] (223 ~84 kinetic {836045448940873}) <223>
[22:08:48.313] [@Heavy Sloth#689203382607232/Arcann {3915326546771968}:26518005427235|(129.37,-323.14,-22.29,-65.76)|(2944/2944)] [@Heavy Sloth#689203382607232|(128.38,-311.39,-22.50,166.71)|(2909/2909)] [Protective Barrier {4238475591155712}] [ApplyEffect {836045448945477}: Heal {836045448945500}] (75 ~15) <5>
[22:08:49.010] [@Igrin#689797178977446|(131.38,-323.90,-22.45,167.92)|(1999/1999)] [Rival Acolyte {287749923930112}:26518003902330|(131.15,-323.95,-22.44,114.22)|(0/240)] [Smash {807801743998976}] [ApplyEffect {836045448945477}: Damage {836045448945501}] (135 ~57 kinetic {836045448940873}) <135>
[21:06:59.259] [@Heavy Sloth#689203382607232|(-523.63,216.71,21.31,-123.11)|(40065/46271)] [Dread Host Detection Probe {3266962578735104}:28040000035058|(-521.73,215.36,21.48,68.35)|(0/2530)] [Vengeful Slam {3503361873674240}] [ApplyEffect {836045448945477}: Damage {836045448945501}] (4887* ~2530 energy {836045448940874}) <4887>
```

Some complete log lines without it for comparison:
```
[21:06:59.258] [@Heavy Sloth#689203382607232|(-523.63,216.71,21.31,-123.11)|(40065/46271)] [Dread Host Soldier {3266932513964032}:28040000024938|(-524.63,218.77,21.40,-25.64)|(1035/7950)] [Vengeful Slam {3503361873674240}] [ApplyEffect {836045448945477}: Damage {836045448945501}] (3024 energy {836045448940874}) <3024>
[21:06:59.259] [@Heavy Sloth#689203382607232|(-523.63,216.71,21.31,-123.11)|(40065/46271)] [Dread Host Soldier {3266932513964032}:28040000024696|(-526.43,215.12,20.76,26.18)|(541/7950)] [Vengeful Slam {3503361873674240}] [ApplyEffect {836045448945477}: Damage {836045448945501}] (3018 energy {836045448940874}) <3018>
[21:06:59.847] [@Heavy Sloth#689203382607232|(-525.49,215.50,20.93,-123.11)|(41340/46271)] [Dread Host Soldier {3266932513964032}:28040000024938|(-524.63,218.77,21.40,-25.64)|(778/7950)] [Bleeding (Draining Scream) {807857578574081}] [ApplyEffect {836045448945477}: Damage {836045448945501}] (257* internal {836045448940876}) <257>
[21:06:59.948] [@Heavy Sloth#689203382607232/Arcann {3915326546771968}:28040000053427|(-527.41,213.12,20.42,-111.65)|(43851/44109)] [@Heavy Sloth#689203382607232|(-525.70,215.36,20.88,-123.11)|(42241/46271)] [Enlivening Force {3770727882817536}] [ApplyEffect {836045448945477}: Heal {836045448945500}] (900) <180>
[21:07:04.676] [@Heavy Sloth#689203382607232/Arcann {3915326546771968}:28040000053427|(-525.87,201.10,19.55,-7.34)|(44109/44109)] [Dread Host Detection Probe {3266962578735104}:28040000029154|(-525.57,198.69,19.39,172.84)|(0/2530)] [Melee Attack {813625719652352}] [ApplyEffect {836045448945477}: Damage {836045448945501}] (814* energy {836045448940874} -shield {836045448945509} (867 absorbed {836045448945511})) <326>
```

Example of why it is probably the effective amount:
```
// Here the Acolyte has 250 health
[22:08:53.710] [Rival Acolyte {287749923930112}:26518003904775|(127.13,-316.76,-21.35,-19.40)|(250/345)] [@Heavy Sloth#689203382607232/Arcann {3915326546771968}:26518005427235|(127.91,-319.00,-21.35,160.70)|(2944/2944)] [Melee Attack {813445331025920}] [ApplyEffect {836045448945477}: Damage {836045448945501}] (0 -parry {836045448945503}) <1>

// Here the effective damage is 251
[22:08:54.227] [@Heavy Sloth#689203382607232|(128.99,-321.23,-21.35,-162.12)|(2891/2909)] [Rival Acolyte {287749923930112}:26518003904775|(127.13,-316.76,-21.35,-19.16)|(0/345)] [Vengeful Slam {3503361873674240}] [ApplyEffect {836045448945477}: Damage {836045448945501}] (590* ~251 energy {836045448940874}) <590>

```