package org.example.enumdemo.withfield;

/**
 * Приклад №2: Enum із полем (рядковим представленням).
 * ✅ Показуємо два варіанти:
 *   - стандартний (toString() = name())
 *   - з перевизначеним toString(), щоб повертати displayName
 */
public class WithFieldDemo {

    public static void main(String[] args) {
        // 1️⃣ Використання стандартного enum (toString() = name())
        Status status = Status.NEW;
        System.out.println("\n1. Standard enum (default toString = name):");
        System.out.println("Status = " + status);               // NEW
        System.out.println("Status.name() = " + status.name()); // NEW
        System.out.println("Status.displayName = " + status.getDisplayName()); // New Task

        // 2️⃣ Використання enum з перевизначеним toString()
        Status2 status2 = Status2.NEW;
        System.out.println("\n2. Enum with overridden toString():");
        System.out.println("Status2 = " + status2);               // "New Task"
        System.out.println("Status2.name() = " + status2.name()); // NEW
        System.out.println("Status2.displayName = " + status2.getDisplayName()); // New Task

        // 3️⃣ Перебір усіх значень
        System.out.println("\n3. All Status values:");
        for (Status s : Status.values()) {
            System.out.println("- " + s + " | name()=" + s.name() + " | displayName=" + s.getDisplayName());
        }

        System.out.println("\n3.1 All Status2 values:");
        for (Status2 s : Status2.values()) {
            System.out.println("- " + s + " | name()=" + s.name() + " | displayName=" + s.getDisplayName());
        }

        // 4️⃣ Пошук по displayName
        Status parsed = Status.fromDisplayName("In progress");
        System.out.println("\n4. Parsed Status from displayName = " + parsed);

        Status2 parsed2 = Status2.fromDisplayName("Completed");
        System.out.println("4. Parsed Status2 from displayName = " + parsed2);
    }
}

/**
 * Варіант 1: стандартний enum із полем
 * ✅ За замовчуванням toString() = name()
 */
enum Status {
    NEW("New Task"),
    IN_PROGRESS("In progress"),
    DONE("Completed");

    // 🔹 Поле — це дані, пов’язані з кожним значенням enum
    private final String displayName;

    // 🔹 Конструктор завжди private (не можна створювати enum ззовні)
    // JVM створює всі enum-константи ще при завантаженні класу
    Status(String displayName) {
        this.displayName = displayName;
    }

    // 🔹 Getter для доступу до поля
    public String getDisplayName() {
        return displayName;
    }

    // 🔹 Статичний метод для пошуку по значенню
    // Корисний, коли маємо рядок (наприклад, з JSON чи БД),
    // але не хочемо напряму викликати valueOf(), бо той кидає виняток на будь-яку невідповідність
    public static Status fromDisplayName(String displayName) {
        for (Status s : values()) {
            if (s.displayName.equalsIgnoreCase(displayName)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Unknown displayName: " + displayName);
    }
}

/**
 * Варіант 2: enum із перевизначеним toString()
 * ✅ Тепер toString() повертає displayName (зручніше для UI, але менш явно для логіки)
 */
enum Status2 {
    NEW("New Task"),
    IN_PROGRESS("In progress"),
    DONE("Completed");

    private final String displayName;

    Status2(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }

    public static Status2 fromDisplayName(String displayName) {
        for (Status2 s : values()) {
            if (s.displayName.equalsIgnoreCase(displayName)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Unknown displayName: " + displayName);
    }
}

/*
📘 Пояснення

🔹 1. Поля
    Ми додаємо поле (percent, description, code тощо) до enum, якщо кожна константа має якеcь додаткове значення, пов’язане з нею.
    Це поле ініціалізується через конструктор для кожної константи.
    Поля у enum зазвичай роблять private final → бо enum незмінні (immutable).

🔹 2. Конструктор
    У enum конструктор завжди private (навіть якщо явно не написано).
    Чому? Тому що екземпляри enum створює JVM автоматично при завантаженні класу.
    Ти не можеш створити enum за допомогою new.
    Конструктор викликається лише для оголошених констант (наприклад, SEASONAL(10)).

🔹 3. Абстрактні методи
    Якщо enum містить абстрактний метод, то кожна константа мусить його реалізувати.
    Це схоже на те, як абстрактний клас змушує підкласи перевизначати методи.
    Enum не можна наслідувати, тому єдине місце для реалізації абстрактного методу — це кожен enum-елемент.

🔹 4. Методи (getter’и)
    Якщо поле приватне, то отримати його можна лише через getter.
    Це стандартна практика, бо напряму percent чи description ззовні доступні не будуть.

📘 Висновки
    Поля в enum потрібні, щоб зберігати дані, що належать кожному елементу.
    Конструктор завжди private → enum створюється лише JVM, не вручну.
    Абстрактний метод змушує кожен елемент реалізувати власну поведінку → це і є стратегія.
    Getter’и потрібні для доступу до полів, бо поля приватні.



    🔹 Enum Status
        Використовує поведінку за замовчуванням:
        System.out.println(Status.NEW) → NEW (бо toString() = name()).
        Для дружнього імені треба явно викликати getDisplayName().

    🔹 Enum Status2

        Перевизначає toString():
        System.out.println(Status2.NEW) → New Task.
        name() все одно лишається технічним (NEW).

    🔹 Best Practice

        Для продакшн-коду частіше краще залишати toString() стандартним і використовувати getDisplayName() → це більш явний і зрозумілий підхід.
        Перевизначати toString() можна для UI, логів чи демо, коли треба “людський” текст.
 */
