# Invernadero con hilos

## Qué hace

Es una simulación de un invernadero para practicar hilos en Java (PSP, UD4). Hay tres sensores y un programador de riego:

- **Sensores** (`Temperatura`, `Humedad`, `Luz`): heredan de la clase abstracta `Sensor`, que implementa `Runnable`. Cada uno toma varias lecturas aleatorias cada cierto tiempo, escribe en el log la hora, el hilo, el sensor y el valor, y marca `⚠ ALERTA` si el valor se sale de sus umbrales. Al final, `informe()` da el número de lecturas, la media, el mínimo, el máximo y las alertas.
- **Riego** (`ProgramadorRiego`): hereda de `Thread` y su hilo se llama `riego`. Hace 5 ciclos alternando la válvula ABIERTA / cerrada cada 700 ms y cuenta los litros (2,5 L cada vez que abre).
- **Vigilante** (`Vigilante`): un `Runnable` con un `while (true)` que cada 300 ms muestra el estado de los cuatro hilos del invernadero.

El proyecto está dividido en fases, cada una con su propio `main`:

| Clase | Fase |
|---|---|
| `Fase1_Secuencial` | Llama a `run()` de todo, uno detrás de otro (la versión lenta). |
| `Fase2_Concurrente` | Cada sensor y el riego van en su propio hilo con `start()`, y `main` espera con `join()`. |
| `Fase3_Experimentos` | Experimentos A, B y C. |
| `Fase4_Vigilante` | Lo mismo que la Fase 2, más el vigilante como hilo daemon. |

Parámetros que he usado:

| Sensor | Periodo | Lecturas | Umbrales |
|---|---|---|---|
| Temperatura | 500 ms | 5 | 15 – 30 °C |
| Humedad | 600 ms | 5 | 40 – 80 % |
| Luz | 400 ms | 5 | 200 – 1500 lux |

## Cómo ejecutarlo

Necesita Java 22. Yo uso Amazon Corretto 22 con IntelliJ IDEA.

**Desde IntelliJ**

1. Abre la carpeta del proyecto (`File → Open`).
2. Comprueba que `src` está marcada como carpeta de código (sale en azul). Si no, haz clic derecho en `src` → `Mark Directory as → Sources Root`.
3. Abre la fase que quieras (por ejemplo `src/invernadero/Fase2_Concurrente.java`) y pulsa el triángulo verde junto a `main`.

**Desde la terminal**, en la carpeta del proyecto:

```bash
javac -encoding UTF-8 -d out src/invernadero/*.java
```

```bash
java -cp out invernadero.Fase2_Concurrente
```

Para otra fase, cambia `Fase2_Concurrente` por `Fase1_Secuencial`, `Fase3_Experimentos` o `Fase4_Vigilante`.

## Captura

Una ejecución de la Fase 2:

```
[08:40:46.509] [riego] Riego ciclo 1: válvula ABIERTA (total 2,5 L)
[08:40:46.509] [sensor-Luz] Luz: 1473,67 lux
[08:40:46.509] [sensor-Temperatura] Temperatura: 22,98 °C
[08:40:46.509] [sensor-Humedad] Humedad: 84,94 %  ⚠ ALERTA
[08:40:46.935] [sensor-Luz] Luz: 1127,10 lux
[08:40:47.030] [sensor-Temperatura] Temperatura: 16,50 °C
[08:40:47.142] [sensor-Humedad] Humedad: 87,88 %  ⚠ ALERTA
[08:40:47.233] [riego] Riego ciclo 2: válvula cerrada (total 2,5 L)
[08:40:47.343] [sensor-Luz] Luz: 841,22 lux
[08:40:47.540] [sensor-Temperatura] Temperatura: 20,30 °C
[08:40:47.749] [sensor-Humedad] Humedad: 53,98 %
[08:40:47.749] [sensor-Luz] Luz: 1085,40 lux
[08:40:47.940] [riego] Riego ciclo 3: válvula ABIERTA (total 5,0 L)
[08:40:48.044] [sensor-Temperatura] Temperatura: 23,28 °C
[08:40:48.165] [sensor-Luz] Luz: 1434,47 lux
[08:40:48.362] [sensor-Humedad] Humedad: 95,00 %  ⚠ ALERTA
[08:40:48.555] [sensor-Temperatura] Temperatura: 21,83 °C
[08:40:48.642] [riego] Riego ciclo 4: válvula cerrada (total 5,0 L)
[08:40:48.967] [sensor-Humedad] Humedad: 49,10 %
[08:40:49.343] [riego] Riego ciclo 5: válvula ABIERTA (total 7,5 L)

===== INFORME =====
Temperatura  | lecturas: 5 | media: 20,98 °C | mín: 16,50 °C | máx: 23,28 °C | alertas: 0
Humedad      | lecturas: 5 | media: 74,18 % | mín: 49,10 % | máx: 95,00 % | alertas: 3
Luz          | lecturas: 5 | media: 1192,37 lux | mín: 841,22 lux | máx: 1473,67 lux | alertas: 0
riego        | ciclos: 5 | litros: 7,5 L
Tiempo total: 3560 ms (3,56 s)
```

Cada línea lleva el nombre del hilo que la ha escrito, y las de distintos hilos salen mezcladas. El informe sale al final porque `main` espera con `join()` a que acaben los cuatro.

## Resultados

He ejecutado tres veces cada fase con los mismos parámetros:

| | Ejecución 1 | Ejecución 2 | Ejecución 3 | Media |
|---|---|---|---|---|
| **Fase 1** (secuencial) | 11,15 s | 11,16 s | 11,17 s | **11,16 s** |
| **Fase 2** (concurrente) | 3,56 s | 3,57 s | 3,58 s | **3,57 s** |

La Fase 2 es unas **3,1 veces más rápida**.

Los números cuadran con las esperas de cada tarea:

- **Fase 1:** el tiempo es la **suma** de todas: 5 × 500 + 5 × 600 + 5 × 400 + 5 × 700 = 2,5 + 3 + 2 + 3,5 = 11 s.
- **Fase 2:** el tiempo es el de la tarea **más larga**, el riego: 5 × 700 ms = 3,5 s.

Lo que sobra en las dos (unos 160 ms y unos 70 ms) es lo que tardan en imprimir, en crear los hilos y en despertar de cada `sleep`.

Otra diferencia: en la Fase 1 el log sale siempre en el mismo orden y todo con `[main]`. En la Fase 2 el orden cambia en cada ejecución. Por ejemplo, las cuatro primeras líneas tienen la misma hora al milisegundo y en cada ejecución salen en un orden distinto: lo decide el planificador del sistema operativo, no el orden de los `start()`.

## Análisis

### ¿Por qué compensa usar hilos en el invernadero?

Porque los sensores y el riego pasan casi todo el tiempo **esperando**, no calculando. Medir un valor aleatorio es instantáneo; lo que tarda es el `Thread.sleep()` entre lecturas. En la Fase 4, de los 280 estados que registró el vigilante, 160 eran `TIMED_WAITING` (durmiendo).

- **En secuencial** esas esperas se suman, porque mientras un sensor duerme nadie más hace nada: 11,16 s.
- **Con hilos** las esperas se solapan: mientras la temperatura duerme, la humedad o la luz están midiendo. El total baja al de la tarea más larga: 3,57 s.

Ni siquiera hace falta tener varios núcleos para notarlo, porque lo que se aprovecha son los tiempos muertos.

Además, en un invernadero de verdad no tiene sentido medir por turnos. En la Fase 1 la temperatura dejaba de medirse durante unos 8,5 s mientras trabajaban los demás sensores y el riego. Si la temperatura se disparara en ese rato, la alerta llegaría tarde. Con hilos cada sensor mide a su ritmo, todos a la vez, y el riego no para a nadie.

### Experimentos A, B y C (qué pasó y por qué)

**A · `informe()` sin esperar al hilo**

```
Sin esperar  -> Temperatura  | sin lecturas
[08:41:20.133] [sensor-Temperatura] Temperatura: 27,05 °C
[08:41:20.451] [sensor-Temperatura] Temperatura: 39,43 °C  ⚠ ALERTA
[08:41:20.764] [sensor-Temperatura] Temperatura: 15,19 °C
Tras join()  -> Temperatura  | lecturas: 3 | media: 27,23 °C | mín: 15,19 °C | máx: 39,43 °C | alertas: 1
```

- **Qué pasó:** la primera vez el informe sale vacío (`sin lecturas`).
- **Por qué:** `start()` no espera a que el hilo haga nada; solo lo pone en marcha y vuelve enseguida. `main` sigue con la siguiente línea e imprime el informe antes de que `sensor-Temperatura` haya tomado su primera lectura. Se ve en que "Sin esperar" sale antes que cualquier lectura. Después de `join()`, `main` se queda parado hasta que el hilo termina, y entonces el informe ya tiene las 3 lecturas.
- **Ojo:** que salga vacío no está garantizado. Si el planificador ejecutara antes el hilo nuevo, podría salir alguna lectura. Por eso hace falta `join()`: sin él, el resultado depende de la suerte.

**B · `riego.run()` en vez de `riego.start()`** (un riego de 3 ciclos)

```
[08:41:21.068] [main] Riego ciclo 1: válvula ABIERTA (total 2,5 L)
[08:41:21.770] [main] Riego ciclo 2: válvula cerrada (total 2,5 L)
[08:41:22.477] [main] Riego ciclo 3: válvula ABIERTA (total 5,0 L)
Tiempo: 2124 ms
riego.getName()  = riego
riego.getState() = NEW
```

- **Qué nombre de hilo sale en el log:** `main`. Llamar a `run()` es una llamada normal a un método y no crea ningún hilo, así que el código del riego lo ejecuta el hilo que llama, `main`. Por eso `main` se queda bloqueado los 2,1 s que dura (3 × 700 ms).
- **El truco de `getName()`:** `riego.getName()` devuelve `riego`, porque es el nombre que guarda el objeto, pero ese hilo nunca ha existido. En el log uso `Thread.currentThread().getName()`, que dice quién está ejecutando de verdad. Si hubiera usado `getName()`, el log diría `[riego]` y parecería que va en su propio hilo, aunque no es así.
- **En qué estado está el objeto:** `NEW`. Un hilo solo sale de `NEW` con `start()`. Como solo hemos llamado a `run()`, la JVM no ha creado el hilo: el objeto sigue "sin estrenar", aunque su código ya se haya ejecutado entero.

**C · `start()` dos veces sobre el mismo objeto**

```
[08:41:23.192] [riego] Riego ciclo 1: válvula ABIERTA (total 2,5 L)
[08:41:23.901] [riego] Riego ciclo 2: válvula cerrada (total 2,5 L)
[08:41:24.608] [riego] Riego ciclo 3: válvula ABIERTA (total 5,0 L)
Estado tras join(): TERMINATED
Excepción capturada: java.lang.IllegalThreadStateException
Estado final: TERMINATED
```

- **Qué excepción salta:** `IllegalThreadStateException`.
- **Qué significa:** se ha llamado a `start()` con el hilo en un estado que no lo permite. Un hilo solo se puede arrancar **una vez**. `start()` comprueba que está en `NEW`, y después del `join()` el hilo está en `TERMINATED`. El ciclo de vida solo va hacia delante (`NEW → RUNNABLE ↔ TIMED_WAITING → TERMINATED`) y de `TERMINATED` no se vuelve.
- **Cómo se trata:** es una excepción no comprobada (hereda de `RuntimeException`), así que el compilador no obliga a capturarla. Si no la capturo, el programa acaba con ella. Para regar otra vez hay que crear otro objeto: `new ProgramadorRiego()`.

### Estados observados (estado · cuándo · qué línea lo provoca)

Datos de 5 ejecuciones de la Fase 4. El vigilante hace una foto de los cuatro hilos cada 300 ms.

| Estado | Veces | Cuándo | Qué línea lo provoca |
|---|---|---|---|
| `NEW` | 20 | Antes de arrancar: el objeto `Thread` existe, pero el hilo aún no. | `new Thread(temperatura, "sensor-Temperatura")` ([Fase4_Vigilante.java:11](src/invernadero/Fase4_Vigilante.java#L11)) y `new ProgramadorRiego()` ([Fase4_Vigilante.java:9](src/invernadero/Fase4_Vigilante.java#L9)) |
| `RUNNABLE` | 26 | Justo al arrancar, y cada vez que un hilo se despierta para medir e imprimir. Dura muy poco, por eso sale poco. | `hTemperatura.start()` … `riego.start()` ([Fase4_Vigilante.java:23-26](src/invernadero/Fase4_Vigilante.java#L23-L26)) y, al despertar, `double valor = medir()` ([Sensor.java:44](src/invernadero/Sensor.java#L44)) |
| `TIMED_WAITING` | 160 | Mientras duerme entre lectura y lectura. Es el más frecuente. | `Thread.sleep(periodo)` ([Sensor.java:58](src/invernadero/Sensor.java#L58)) y `Thread.sleep(PAUSA)` ([ProgramadorRiego.java:45](src/invernadero/ProgramadorRiego.java#L45)) |
| `BLOCKED` | 4 | Solo en la primera foto, en 4 de las 5 ejecuciones. Al arrancar, los cuatro hilos quieren escribir a la vez y `println` es `synchronized`: mientras uno escribe, los demás esperan el cerrojo de `System.out`. | `System.out.println(linea)` ([Sensor.java:55](src/invernadero/Sensor.java#L55)) |
| `TERMINATED` | 70 | Cuando acaba el bucle y `run()` termina. Luz acaba la primera (5 × 400 ms) y el riego el último (5 × 700 ms). | El final del `for` de `run()` ([Sensor.java:43](src/invernadero/Sensor.java#L43), [ProgramadorRiego.java:32](src/invernadero/ProgramadorRiego.java#L32)) |

`WAITING` no me ha salido. Es el estado de un hilo que espera sin límite de tiempo. Lo tiene `main` mientras está en `hTemperatura.join()` ([Fase4_Vigilante.java:29](src/invernadero/Fase4_Vigilante.java#L29)), pero el vigilante solo vigila los cuatro hilos del invernadero, no a `main`.

Antes de `start()` los cuatro están en `NEW`, y después de los `join()` los cuatro están en `TERMINATED`:

```
Antes de start():  sensor-Temperatura=NEW | sensor-Humedad=NEW | sensor-Luz=NEW | riego=NEW
[08:41:25.485] [vigilante] sensor-Temperatura=RUNNABLE | sensor-Humedad=RUNNABLE | sensor-Luz=BLOCKED | riego=RUNNABLE
[08:41:25.796] [vigilante] sensor-Temperatura=TIMED_WAITING | sensor-Humedad=TIMED_WAITING | sensor-Luz=TIMED_WAITING | riego=TIMED_WAITING
...
[08:41:27.650] [vigilante] sensor-Temperatura=TIMED_WAITING | sensor-Humedad=TIMED_WAITING | sensor-Luz=TERMINATED | riego=TIMED_WAITING
...
[08:41:28.876] [vigilante] sensor-Temperatura=TERMINATED | sensor-Humedad=TERMINATED | sensor-Luz=TERMINATED | riego=TIMED_WAITING
Después de join(): sensor-Temperatura=TERMINATED | sensor-Humedad=TERMINATED | sensor-Luz=TERMINATED | riego=TERMINATED
vigilante: TIMED_WAITING, daemon = true
main termina y la JVM se cierra sin esperar al vigilante
```

**¿Por qué termina el programa si el vigilante sigue en su bucle?**

La JVM no se cierra mientras quede vivo algún hilo normal (*user thread*). Si el vigilante fuera normal, al acabar los sensores y el riego seguiría en su `while (true)` y el programa no terminaría nunca.

Por eso el vigilante es un hilo **daemon**: `vigilante.setDaemon(true)` ([Fase4_Vigilante.java:18](src/invernadero/Fase4_Vigilante.java#L18)). Un daemon es un hilo "de servicio" al que la JVM no espera. Cuando terminan `main` y los cuatro hilos del invernadero, solo queda el vigilante, y la JVM se cierra y lo corta en mitad del bucle. En el log se ve que justo antes de cerrarse sigue vivo, en `TIMED_WAITING` (durmiendo). Las 5 ejecuciones terminaron solas en unos 3,7 s.

`setDaemon(true)` hay que llamarlo **antes de `start()`**. Con el hilo ya arrancado lanza `IllegalThreadStateException`, la misma excepción que en el experimento C.

### Thread frente a Runnable

En el proyecto uso las dos formas: el riego hereda de `Thread` y los sensores implementan `Runnable`.

| Aspecto | `extends Thread` (`ProgramadorRiego`) | `implements Runnable` (`Sensor`) |
|---|---|---|
| Qué representa el objeto | El **hilo**: el trabajador y la tarea van juntos en el mismo objeto. | Solo la **tarea**. El hilo es otro objeto, un `Thread`, que la ejecuta. |
| Cómo se arranca | `riego.start()` | `new Thread(temperatura, "sensor-Temperatura").start()` |
| Cómo se le da nombre al hilo | En su constructor: `super("riego")` | Al crear el `Thread`: `new Thread(tarea, "sensor-Luz")` |
| Cómo se obtiene el nombre del hilo | `getName()`, heredado de `Thread`. Ojo: devuelve el nombre del objeto aunque lo esté ejecutando `main` (experimento B). | No tiene `getName()`: `Thread.currentThread().getName()` |
| Herencia | Gasta la única herencia que permite Java: la clase ya no puede heredar de otra. | Es una interfaz: la clase puede heredar de otra (`Temperatura extends Sensor`) e implementar todas las interfaces que quiera. |
| Reutilización | Un objeto solo se puede arrancar una vez (experimento C). Para repetir hay que crear otro. | La misma tarea se puede dar a un `Thread` nuevo, o a un pool de hilos. |
| Acceso a `join()`, `getState()`… | Directo, sobre el propio objeto: `riego.join()` | Hay que guardar el `Thread` en una variable: `hTemperatura.join()` |

**¿Por qué los sensores SOLO pueden ser `Runnable` en mi diseño?**

Porque `Temperatura`, `Humedad` y `Luz` ya heredan de `Sensor`, la clase abstracta que tiene los atributos comunes (nombre, unidad, periodo, umbrales), las estadísticas, `run()` e `informe()`. Java solo permite heredar de **una** clase, así que no pueden heredar a la vez de `Sensor` y de `Thread`. Esa herencia ya está ocupada.

`Runnable` es una interfaz, y las interfaces no ocupan la herencia. `Sensor` la implementa y las tres subclases la heredan sin más. Así cada sensor es una tarea que puedo meter en el hilo que quiera, como hago en la Fase 2.

**¿Qué forma elegiría para el riego y por qué?**

Lo he hecho con `extends Thread` porque lo pedía el enunciado, y para una tarea tan simple es cómodo: el nombre va en el constructor y se arranca con `riego.start()`. Pero si lo eligiera yo, usaría **`Runnable`**:

- **Coherencia:** todo el invernadero se lanzaría igual, con `new Thread(tarea, nombre)`.
- **Herencia libre:** si más adelante hubiera más actuadores (riego, ventilación, calefacción), podrían heredar de una clase común `Actuador`, como los sensores heredan de `Sensor`. Con `extends Thread` eso no se puede.
- **Separación de la tarea y el hilo:** el riego describiría *qué* hay que hacer y el `Thread` *quién* lo ejecuta. No tendría el problema del experimento C (para repetir bastaría con un `new Thread(riego)` nuevo), ni el engaño de `getName()` del experimento B.
