package org.example.enumdemo.strategy;

/**
 * Приклад №4: Enum як Strategy (Demo 2).
 *
 * ✅ Ідея:
 *   - Маємо різні "стратегії розрахунку знижки" (DiscountType)
 *   - Кожен enum-елемент реалізує свій спосіб розрахунку
 *   - Це міні-патерн Strategy прямо в enum
 *
 * ❓ Навіщо так робити?
 *   - Якщо набір варіантів відомий і обмежений (наприклад, типи знижок)
 *   - Кожен варіант має унікальну поведінку (свою формулу)
 *   - Ми хочемо уникнути десятків if-else чи switch
 *
 * ⚠️ Коли не варто?
 *   - Якщо варіанти часто змінюються → краще винести в окремі класи
 *   - Якщо логіка дуже складна → enum стане "God object"
 */
public class StrategyDemo2 {

    public static void main(String[] args) {
        double basePrice = 100.0;

        // 1️⃣ Використання знижки SEASONAL
        double seasonalPrice = DiscountType.SEASONAL.applyDiscount(basePrice);
        System.out.println("1. Seasonal discount: " + seasonalPrice);

        // 2️⃣ Використання знижки LOYALTY
        double loyaltyPrice = DiscountType.LOYALTY.applyDiscount(basePrice);
        System.out.println("2. Loyalty discount: " + loyaltyPrice);

        // 3️⃣ Без знижки
        double noDiscount = DiscountType.NONE.applyDiscount(basePrice);
        System.out.println("3. No discount: " + noDiscount);

        // 4️⃣ Перебір усіх стратегій
        System.out.println("\nAvailable discount types:");
        for (DiscountType type : DiscountType.values()) {
            System.out.println("- " + type.name() + " | percent=" + type.getPercent());
        }
    }
}

/**
 * Enum DiscountType демонструє використання Strategy.
 *
 * 🔹 Кожен елемент enum має:
 *   - поле percent (розмір знижки)
 *   - свою реалізацію методу applyDiscount()
 *
 * ✅ Це зручно, бо:
 *   - Весь код зібраний в одному місці
 *   - Легко додати нову знижку (просто створити новий елемент enum)
 *   - Код, який використовує DiscountType, не міняється
 */
enum DiscountType {
    SEASONAL(10) {
        @Override
        public double applyDiscount(double price) {
            return price - (price * getPercent() / 100.0);
        }
    },
    LOYALTY(5) {
        @Override
        public double applyDiscount(double price) {
            return price - (price * getPercent() / 100.0);
        }
    },
    NONE(0) {
        @Override
        public double applyDiscount(double price) {
            return price; // без знижки
        }
    };

    private final int percent;

    DiscountType(int percent) {
        this.percent = percent;
    }

    public int getPercent() {
        return percent;
    }

    /**
     * Абстрактний метод, який реалізується по-різному для кожного enum-елемента.
     * Це і є "стратегія".
     */
    public abstract double applyDiscount(double price);
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

🔹 Чому enum = Strategy?
    У класичному патерні Strategy є інтерфейс (наприклад, DiscountStrategy), а кожен конкретний клас реалізує свою поведінку.
    У випадку enum: ми "вбудували" реалізації прямо у константи → кожна константа = міні-клас зі своєю поведінкою.
    Таким чином ми позбулися купи if-else чи switch у коді.

🔹 Коли це зручно?
    Фіксований набір варіантів (знижки, валюти, типи нотифікацій, стани задачі).
    Кожен варіант має свою поведінку (різні формули, правила).
    Простота важливіша за гнучкість (нам не потрібно динамічно підвантажувати нові стратегії).

🔹 Плюси
    ✅ Лаконічність — все в одному місці.
    ✅ Код легко читати: відразу видно всі можливі варіанти і їхню поведінку.
    ✅ Легко додати новий тип: просто додати новий елемент у enum.

🔹 Мінуси
    ❌ Якщо стратегій дуже багато → enum стане занадто великим.
    ❌ Якщо поведінка складна → код важко підтримувати (краще окремі класи).
    ❌ Не підходить для динамічних стратегій (наприклад, з БД чи конфігів).

🔹 Порівняння з StrategyDemo1

    Demo1: приклад з NotificationType (EMAIL/SMS/PUSH).
        → Показує поведінку, але більше як "різні канали комунікацій".

    Demo2: приклад із DiscountType (знижки).
        → Ближче до бізнес-логіки (формули/розрахунки).

📌 Висновок:
    Якщо варіанти обмежені і рідко змінюються → enum-стратегія = ✔️.
    Якщо логіка велика і складна → краще інтерфейс + окремі класи (чистий патерн Strategy).
 */