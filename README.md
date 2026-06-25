# Robot Labyrinth

Клиент–серверная игра: робот перемещается по трёхмерному лабиринту, а его поведение задаётся программой на собственном языке. Проект демонстрирует разработку интерпретатора (лексер, парсер, семантический анализ, runtime), сетевого протокола и многомодульной Java-архитектуры.

## Возможности

- **3D-лабиринт** — уровни по оси Z, препятствия (`#`), выходы (`E`); столкновение ломает робота
- **Язык управления роботом** — типы, функции, циклы, условия, массивы; команды движения и измерения расстояния
- **TCP-сервер** — JSON-протокол, многопоточная обработка клиентов
- **Интерпретатор** — подключается к серверу и выполняет `.robot`-программы
- **Консольный клиент** — ручное управление роботом для отладки

## Архитектура

```
robot-labyrinth/
├── robot-common/         — протокол, клиент, общие модели
├── robot-game-server/    — игровой движок и TCP-сервер
├── robot-interpreter/    — лексер (JFlex), парсер (CUP), runtime
└── robot-console-client/ — интерактивный CLI-клиент
```

| Модуль | Назначение |
|--------|------------|
| `robot-common` | `RobotClient`, JSON-команды (`MOVE`, `MEASURE`, `GET_POSITION`, `GET_LABYRINTH`) |
| `robot-game-server` | Загрузка лабиринта, логика перемещения, `ClientHandler` |
| `robot-interpreter` | Полный pipeline: лексический и синтаксический анализ → AST → семантика → исполнение |
| `robot-console-client` | Ручные команды и визуализация карты |

## Стек

Java 17 · Maven · JFlex · Java CUP · org.json · JUnit 4

## Сборка и запуск

**Требования:** JDK 17, Maven 3.6+

```bash
# Сборка всех модулей
mvn clean package

# Тесты интерпретатора
mvn test -pl robot-interpreter
```

### 1. Запуск сервера

```bash
mvn exec:java -pl robot-game-server \
  -Dexec.mainClass=com.lab.robot.game.server.GameServer
```

По умолчанию: порт `8080`, лабиринт `src/main/resources/labyrinth.txt`.

### 2. Запуск программы робота

```bash
mvn exec:java -pl robot-interpreter \
  -Dexec.mainClass=com.lab.robot.interpreter.main.InterpreterMain \
  -Dexec.args="robot-interpreter/src/main/resources/programs/solve.robot"
```

Флаги: `--host`, `--port`, `--quiet`, `--no-map`.

### 3. Консольный клиент (опционально)

```bash
mvn exec:java -pl robot-console-client \
  -Dexec.mainClass=com.lab.robot.client.ConsoleUI
```

## Язык робота

Ключевые слова — японская транслитерация (`seisu` — int, `kansu` — функция, `shuki` — цикл, `sorenara` — if). Команды робота записываются ASCII-артом:

| Команда | Действие |
|---------|----------|
| `o_o` / `~_~` | вперёд / назад |
| `<_<` / `>_>` | влево / вправо |
| `^_^` / `v_v` | вверх / вниз |
| `o_0` | измерить расстояние вперёд |
| `*_*` | получить позицию (`x`, `y`, `z`, `exit`) |

```robot
seisu kansu solve() kido
  rippotai p = *_*;
  sorenara p=>exit kido
    return 1;
  shushi;
  o_o;
  return solve();
shushi;

solve();
```

В репозитории есть полный пример поиска выхода методом backtracking: `robot-interpreter/src/main/resources/programs/solve.robot`.

## Формат лабиринта

Первая строка: `sizeX sizeY sizeZ startX startY startZ`. Далее — слои по Z, по `sizeY` строк в каждом:

- `.` — проходимая клетка
- `#` — препятствие
- `E` — выход

## Тестирование

Модуль `robot-interpreter` покрыт unit-тестами: лексер, парсер, семантический анализ, runtime (включая рекурсию, массивы и разбор `solve.robot`).
