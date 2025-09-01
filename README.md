📘 Enum у Java – повний розбір

🔹 Теорія: що таке enum

    - enum (enumeration) = спеціальний клас, який обмежує значення лише наперед визначеним набором констант.
    
    - Використовується для:
        - ролей (ADMIN, USER, DRIVER)
        - статусів (NEW, IN_PROGRESS, DONE)
        - фіксованих параметрів (наприклад, типи документів, країни, валюти).
    
    - Enum у Java є immutable (незмінний) і створюється JVM автоматично при старті → вручну new створювати не можна.

🔹 Основні можливості enum

1️⃣ Кілька констант

    enum TransportType {
        BUS,
        TRAIN,
        TAXI
    }

✅ Використовується, коли є обмежений список можливих варіантів.

    - Типобезпека: role == Role.ADMIN краще ніж "ADMIN".equals(role)
    - Використання в switch-case і колекціях.

📌 Коли застосовувати

    - Усі прості довідники: ролі, статуси, типи документів.


2️⃣ Поля (дані, що описують константу)

    enum TransportType {
        BUS(2.5, "BUS"),
        TRAIN(1.8, "TRN"),
        TAXI(10.0, "TXI");
    
        private final double baseRate;
        private final String code;
    
        TransportType(double baseRate, String code) {
            this.baseRate = baseRate;
            this.code = code;
        }
    
        public double getBaseRate() { return baseRate; }
        public String getCode() { return code; }
    }


✅ Тепер кожна константа має власні параметри.

    - BUS → тариф 2.5, код "BUS".
    - TAXI → тариф 10.0, код "TXI".

📌 Коли застосовувати

    - Якщо константи потребують додаткових даних (наприклад, код ISO країни, опис, тариф).


3️⃣ Звичайний метод (спільна логіка)

    public double calculateCost(double distanceKm) {
        return baseRate * distanceKm;
    }


✅ Дозволяє винести загальну логіку в enum.

    - Тепер можна порахувати вартість поїздки для будь-якого транспорту:

        double cost = TransportType.BUS.calculateCost(10); // 25.0


📌 Коли застосовувати

    - Якщо всі константи мають однакову поведінку, яка залежить лише від їхніх полів.


4️⃣ Абстрактний метод (різна поведінка для кожної константи)
    
    public abstract String extraInfo();
    
    BUS(2.5, "BUS") {
        @Override
        public String extraInfo() {
            return "Bus is cheap but slower";
        }
    },
    TRAIN(1.8, "TRN") {
        @Override
        public String extraInfo() {
            return "Train is fast and economical";
        }
    },
    TAXI(10.0, "TXI") {
        @Override
        public String extraInfo() {
            return "Taxi is comfortable but expensive";
        }
    };


✅ Кожна константа може мати власну реалізацію методу.

    - BUS.extraInfo() → "Bus is cheap but slower"
    - TAXI.extraInfo() → "Taxi is comfortable but expensive"

📌 Коли застосовувати

    - Якщо константи мають різну поведінку (наприклад, різні формули розрахунку, повідомлення, бізнес-логіка).
    - Це по суті mini-реалізація патерну Strategy прямо в enum.


5️⃣ Статичний метод (фабрика / пошук)

    public static TransportType fromCode(String code) {
        for (TransportType t : values()) {
            if (t.code.equalsIgnoreCase(code)) {
                return t;
            }
        }
        throw new IllegalArgumentException("Unknown code: " + code);
    }


✅ Дозволяє знайти константу по зовнішньому значенню (наприклад, по коду).

    TransportType t = TransportType.fromCode("TXI"); // TAXI


📌 Коли застосовувати

    Для пошуку enum за значенням із JSON, БД чи API.
    
    Це краща альтернатива ручному перебору values() у кожному місці коду.

🔹 Повний приклад коду
    
    enum TransportType {
        BUS(2.5, "BUS") {
            @Override
            public String extraInfo() {
                return "Bus is cheap but slower";
            }
        },
        TRAIN(1.8, "TRN") {
            @Override
            public String extraInfo() {
                return "Train is fast and economical";
            }
        },
        TAXI(10.0, "TXI") {
            @Override
            public String extraInfo() {
                return "Taxi is comfortable but expensive";
            }
        };
    
        private final double baseRate;
        private final String code;
    
        TransportType(double baseRate, String code) {
            this.baseRate = baseRate;
            this.code = code;
        }
    
        public double getBaseRate() { return baseRate; }
        public String getCode() { return code; }
    
        public double calculateCost(double distanceKm) {
            return baseRate * distanceKm;
        }
    
        public abstract String extraInfo();
    
        public static TransportType fromCode(String code) {
            for (TransportType t : values()) {
                if (t.code.equalsIgnoreCase(code)) {
                    return t;
                }
            }
            throw new IllegalArgumentException("Unknown code: " + code);
        }
    }

📌 Фінальний висновок

    - Константи → обмежений список значень (типобезпека).
    - Поля → додаткові дані для кожної константи.
    - Звичайний метод → спільна логіка для всіх.
    - Абстрактний метод → різна поведінка для кожної константи.
    - Статичний метод → утиліта (фабрика, пошук по значенню).