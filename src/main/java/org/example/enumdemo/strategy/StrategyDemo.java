package org.example.enumdemo.strategy;

/**
 * Приклад №3: Enum як mini-Strategy.
 *
 * ✅ Тут ми показуємо:
 *   - як у enum можна оголосити абстрактний метод
 *   - як кожна константа реалізує свою поведінку
 *   - як це заміняє класичний патерн Strategy у простих кейсах
 */
public class StrategyDemo {

    public static void main(String[] args) {
        // 1️⃣ Використання стратегії EMAIL
        NotificationType email = NotificationType.EMAIL;
        email.send("Hello from email strategy!");

        // 2️⃣ Використання стратегії SMS
        NotificationType sms = NotificationType.SMS;
        sms.send("Hello from SMS strategy!");

        // 3️⃣ Використання стратегії PUSH
        NotificationType push = NotificationType.PUSH;
        push.send("Hello from push strategy!");

        // 4️⃣ Перебір усіх стратегій
        System.out.println("\nAvailable notification types:");
        for (NotificationType type : NotificationType.values()) {
            System.out.println("- " + type.name() + " | description=" + type.getDescription());
        }
    }
}

/**
 * Enum-стратегія для різних типів нотифікацій.
 *
 * Кожен тип має:
 *  - поле description (для людини або UI)
 *  - власну реалізацію методу send()
 */
enum NotificationType {
    EMAIL("Email notification") {
        @Override
        public void send(String message) {
            System.out.println("[EMAIL] Sending email with message: " + message);
        }
    },
    SMS("SMS notification") {
        @Override
        public void send(String message) {
            System.out.println("[SMS] Sending SMS with message: " + message);
        }
    },
    PUSH("Push notification") {
        @Override
        public void send(String message) {
            System.out.println("[PUSH] Sending push notification: " + message);
        }
    };

    private final String description;

    NotificationType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    // 🔹 абстрактний метод, який має реалізувати кожна константа
    public abstract void send(String message);
}

/*
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



    📘 Теоретичні пояснення

        🔹 Ключова ідея

            Enum може мати абстрактні методи.
            Кожна константа enum повинна реалізувати ці методи окремо.
            Таким чином ми отримуємо різну поведінку для кожної константи → це і є mini-Strategy.

        🔹 Навіщо так робити?

            Якщо набір стратегій фіксований і відомий наперед (наприклад, EMAIL, SMS, PUSH).
            Замість створення окремих класів для кожної стратегії можна все тримати в одному enum.
            Це спрощує код у простих кейсах.

        🔹 Коли не варто?

            Якщо стратегій багато або вони часто змінюються → краще класичний патерн Strategy з інтерфейсом + окремими класами.
            Якщо поведінка складна й велика → краще винести у сервіси чи окремі класи, щоб enum не був “переповнений”.

    📌 Висновок

        У цьому прикладі enum = маленький контейнер для даних (description) і поведінки (send).
        Це спрощений варіант патерну Strategy, корисний для випадків, коли стратегій небагато і вони відомі наперед.
        У Spring Boot так часто роблять для notification type, export type, calculation type, де кілька варіантів поведінки треба відрізняти чітко.
 */
