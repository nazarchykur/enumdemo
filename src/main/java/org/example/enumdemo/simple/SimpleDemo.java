package org.example.enumdemo.simple;


/**
 * Найпростіший приклад роботи з enum.
 * Тут ми просто показуємо:
 * - як вибрати enum
 * - як використати в switch
 * - як перебрати всі значення
 */
public class SimpleDemo {

    public static void main(String[] args) {
        // 1️⃣ Вибір конкретного значення
        Role role = Role.ADMIN;
        System.out.println("\n1. Selected role = " + role);

        // 2️⃣ Використання в switch
        switch (role) {
            case ADMIN -> System.out.println("2. Admin can manage users");
            case USER -> System.out.println("2. User can view and edit profile");
            case DRIVER -> System.out.println("2. Driver can view tasks");
        }

        // 3️⃣ Перебір усіх значень enum
        // values() → усі значення (часто для UI/списків).
        System.out.println("\n3. Available roles:");
        for (Role r : Role.values()) {
            System.out.println("- " + r);
        }

        // 4️⃣ Детальніший перебір (ordinal + name + toString)
        /*
            Відмінність між r і r.name()

            System.out.println(r) під капотом викликає r.toString().
                За замовчуванням у enum toString() = name().

            System.out.println(r.name())
                викликає name() напряму (це завжди оригінальне ім’я константи).

            👉 Тобто зараз вони однакові.
        */
        System.out.println("\n4. Available roles with extra info:");
        for (Role r : Role.values()) {
            System.out.println("- " + r
                    + " | ordinal=" + r.ordinal()
                    + " | name()=" + r.name()
                    + " | toString()=" + r.toString());
        }

        /*
            Але різниця з’являється, якщо ми перевизначимо toString() у enum.
            For Role 2 override toString

            name() = завжди сире ім’я константи.
            toString() = можна кастомізувати для красивого виводу (але не для логіки).
        */
        System.out.println("\n4.1. Available roles with extra info with overridden toString:");
        for (Role2 r : Role2.values()) {
            System.out.println("- " + r);
        }

        // 5️⃣ Використання valueOf (рядок → enum)
        // valueOf(String) → конвертувати рядок у enum. ⚠️ Небезпечно з користувацьким вводом (кидає IllegalArgumentException).
        Role parsed = Role.valueOf("USER");
        System.out.println("\n5. Parsed from String = " + parsed);

        // 6️⃣ Порівняння enum-ів (типобезпечно)
        // equals()/hashCode() → успадковані з Object, але зазвичай не потрібні (бо == достатньо).
        if (role == Role.ADMIN) {
            System.out.println("\n6. Role is ADMIN (compared with ==)");
        }

        // 7️⃣ Використання compareTo (порівняння порядку)
        // compareTo() → порівняння порядку між enum-константами (по ordinal). ❌(рідко в бізнес-логіці, більше в утилітах).
        int cmp = Role.ADMIN.compareTo(Role.USER);
        System.out.println("\n7. CompareTo(Admin vs User) = " + cmp);

        // 8️⃣ Використання getDeclaringClass (отримати клас enum)
        // getDeclaringClass() → у generic-утилітах чи reflection-based логіці.
        System.out.println("\n8. Declaring class = " + Role.ADMIN.getDeclaringClass());
        System.out.println("8. Declaring class = " + Role2.ADMIN.getDeclaringClass());
        System.out.println("8. Declaring class = " + Role2.class.getSimpleName());

        // 9️⃣ Використання hashCode і equals (звичайні методи Object)
        System.out.println("\n9. HashCode of ADMIN = " + Role.ADMIN.hashCode());
        System.out.println("9. HashCode of USER = " + Role.USER.hashCode());
        System.out.println("9. Equals(Admin, User) = " + Role.ADMIN.equals(Role.USER));
    }
}

/**
 * Very basic enum example.
 * ✅ Використовується для обмеженого набору значень
 */
enum Role {
    ADMIN,
    USER,
    DRIVER
}

enum Role2 {
    ADMIN, USER, DRIVER;

    @Override
    public String toString() {
        return "Role2: " + name();
    }
}


/*
* Теоретичні пояснення до методів
    ✅ Основні методи enum:

        values() → усі значення (часто для UI/списків).

        ordinal() → позиція (0,1,2). ⚠️ Не зберігати у БД, бо порядок можна змінити.

        name() → завжди сире ім’я константи (безпечний для логіки).

        toString() → дефолтно як name(), але можна перевизначити для красивого виводу.

        valueOf(String) → конвертувати рядок у enum. ⚠️ Небезпечно з користувацьким вводом (кидає IllegalArgumentException).

        compareTo() → порівняння порядку між enum-константами (по ordinal).

        getDeclaringClass() → повертає клас enum (корисно для рефлексії чи generic-методів).

        equals()/hashCode() → успадковані з Object, але зазвичай не потрібні (бо == достатньо).


    🔹 В яких ситуаціях що використовувати?

        values() → коли потрібен список усіх значень (UI drop-down, документація, автогенерація).

        ordinal() → для внутрішніх алгоритмів, але ⚠️ ніколи для зберігання у БД.

        name() → для стабільного логічного використання (наприклад, мапінг рядків).

        toString() → для гарного відображення користувачу (UI, логи).

        valueOf() → коли конвертуємо з відомого, перевіреного рядка (наприклад, з БД).

        compareTo() → коли треба порівняти порядок (рідко в бізнес-логіці, більше в утилітах).

        getDeclaringClass() → у generic-утилітах чи reflection-based логіці.




    🔹 getDeclaringClass() vs class.getSimpleName()

        1. Role.ADMIN.getDeclaringClass()

            Це метод з enum-значення (інстансу).
            Він повертає Class об’єкт, який "оголосив" цей enum-елемент.

            System.out.println(Role.ADMIN.getDeclaringClass());
            // output: org.example.enumdemo.simple.Role

            ⚡ Використання:
                коли у тебе є конкретне значення (Role.ADMIN), але ти хочеш отримати клас enum, до якого воно належить;
                часто в generic методах чи reflection, коли ти не знаєш клас заздалегідь.

        🔹 2. Role.class.getSimpleName()

            Це робота напряму з класом (через мета-інформацію).
            Role.class → це сам клас Role.
            getSimpleName() повертає просто ім’я класу без пакету → "Role".

            System.out.println(Role.class.getSimpleName());
            // output: Role

            ⚡ Використання:
                коли ти знаєш клас наперед і хочеш просто його назву (наприклад, для логування, генерації повідомлень).
                для відображення користувачу чи в документації.

        🔹 Основна різниця

            getDeclaringClass() → від конкретного enum-значення → дає Class (можна далі робити .getSimpleName(), .getPackageName(), .getName()).

            Role.class.getSimpleName() → напряму від класу → дає вже рядок із назвою класу.

        🔹 Приклади різниці у використанні
            // маємо конкретне значення
            Role role = Role.ADMIN;

            // варіант 1: отримуємо клас з інстансу
            Class<?> clazz = role.getDeclaringClass();
            System.out.println(clazz);                // class Role
            System.out.println(clazz.getSimpleName()); // Role

            // варіант 2: працюємо напряму з класом
            System.out.println(Role.class.getSimpleName()); // Role


        👉 Якщо у тебе вже є змінна role (типу Role), зручно використовувати role.getDeclaringClass().
        👉 Якщо ти просто працюєш із самим класом, то логічніше Role.class.getSimpleName().
*/