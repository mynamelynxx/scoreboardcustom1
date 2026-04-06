# Scoreboard Changer — Fabric 1.21.4

Мод для визуальной замены значений scoreboard на серверах Minecraft.

## Что умеет

- Визуально заменяет строки scoreboard (не создаёт свой — лишь перехватывает рендер)
- Кастомное меню настроек, открывается по биндингу
- Заменяет: **никнейм**, **ранг** (с выбором цвета), **монеты**, **токены**, **черепки**, **убийства**, **смерти**, **наиграно**
- Режим сервера (Анархия — 505 и т.п.) берётся из оригинального scoreboard сервера — мод его не трогает
- Все настройки сохраняются в `config/scoreboardchanger.json`

## Установка

1. Установи [Fabric Loader](https://fabricmc.net/use/installer/) для 1.21.4
2. Установи [Fabric API](https://modrinth.com/mod/fabric-api)
3. Скинь `.jar` мода в папку `mods/`

## Бинд

Зайди в **Настройки → Управление** и найди `Scoreboard Changer: Открыть меню`. Поставь любую клавишу.

## Сборка через GitHub

1. Форкни / запушь проект на GitHub
2. GitHub Actions автоматически соберёт `.jar` при каждом пуше
3. Скачай артефакт в разделе **Actions → последний билд → Artifacts**

## Локальная сборка

```bash
./gradlew build
```

Готовый `.jar` будет в `build/libs/scoreboardchanger-1.0.0.jar`.

## Структура проекта

```
src/
  main/java/com/scoreboardchanger/
    config/ModConfig.java          — хранение и сохранение настроек
    mixin/
      ScoreboardObjectiveRendererMixin.java  — перехват рендера строк scoreboard
      PlayerListHudMixin.java                — заглушка для будущих фич
  client/java/com/scoreboardchanger/
    ScoreboardChangerClient.java   — точка входа, регистрация биндинга
    gui/ScoreboardChangerScreen.java — экран настроек
```

## Важно

Мод работает **только визуально** на клиенте. Сервер ничего не видит.
Строки определяются по ключевым словам (`Ранг:`, `Монет:`, `Убийств:` и т.д.) — если на твоём сервере названия другие, поправь `ScoreboardObjectiveRendererMixin.java`.
