package org.example.enumdemo.withjson;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * DTO приклад із enum
 * Показуємо різні варіанти серіалізації/десеріалізації з Jackson
 */
public class EnumJacksonDemo {

    public static void main(String[] args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        // 1️⃣ Серіалізація DTO → JSON
        EmployeeDto dto = new EmployeeDto("Nazar", Role.ADMIN, EmploymentType.FULL_TIME);
        String json = mapper.writeValueAsString(dto); // Серіалізація
        System.out.println("1. DTO → JSON:");
        System.out.println(json);

        // 2️⃣ Десеріалізація JSON → DTO
        // "employmentType": "Part time"   бо displayName
        String inputJson = """
                {
                  "name": "Nazar",
                  "role": "USER",
                  "employmentType": "Part time"
                }
                """;
        EmployeeDto dto2 = mapper.readValue(inputJson, EmployeeDto.class);
        System.out.println("\n2. JSON → DTO:");
        System.out.println(dto2);

        // 3️⃣ Десеріалізація JSON → DTO, але тут використовуємо тип

        String inputJson2 = """
                {
                  "name": "Nazar",
                  "role": "USER",
                  "employmentType": "PART_TIME"
                }
                """;
        EmployeeDto dto3 = mapper.readValue(inputJson2, EmployeeDto.class);
        System.out.println("\n3. JSON → DTO:");
        System.out.println(dto3);
    }
}

// DTO з enum полями
class EmployeeDto {
    private String name;
    private Role role;
    private EmploymentType employmentType;

    public EmployeeDto() {
    }

    public EmployeeDto(String name, Role role, EmploymentType employmentType) {
        this.name = name;
        this.role = role;
        this.employmentType = employmentType;
    }

    public String getName() {
        return name;
    }

    public Role getRole() {
        return role;
    }

    public EmploymentType getEmploymentType() {
        return employmentType;
    }

    @Override
    public String toString() {
        return "EmployeeDto{name='" + name + "', role=" + role + ", employmentType=" + employmentType + "}";
    }
}

enum Role {
    ADMIN,
    USER,
    DRIVER
}

/**
 * Enum із кастомним відображенням
 */
enum EmploymentType {
    FULL_TIME("Full time"),
    PART_TIME("Part time"),
    CONTRACTOR("Contractor");

    private final String displayName;

    EmploymentType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    // 🔹 За замовчуванням у JSON буде FULL_TIME, PART_TIME, CONTRACTOR
    // Якщо хочемо відображати displayName → додаємо @JsonValue
    @JsonValue
    public String toJson() {
        return displayName; // "Full time", "Part time", "Contractor"
    }

    // 🔹 Для десеріалізації з "Full time" назад у enum
    // Jackson дивиться на @JsonCreator → шукає EmploymentType по displayName → знаходить "Part time" → повертає EmploymentType.PART_TIME.
    @JsonCreator
    public static EmploymentType fromJson(String value) {
        for (EmploymentType type : values()) {
            if (type.displayName.equalsIgnoreCase(value) || type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }
}


/*
🔹 Коли такий підхід ідеальний?
    Якщо цей JSON йде у зовнішній API/UI → там краще показати "Part time", а не "PART_TIME".
    Якщо треба чітко відділити технічну назву (name) і людське представлення (displayName).
    Якщо у тебе однаковий enum використовується і в БД (name()), і в API (displayName).

🔹 Мінус
    Тепер JSON не прийме "PART_TIME" напряму, бо @JsonCreator перевіряє тільки displayName.
    Але це можна виправити так якщо додати ... || type.name().equalsIgnoreCase(value):

    @JsonCreator
    public static EmploymentType fromJson(String value) {
        for (EmploymentType type : values()) {
            if (type.displayName.equalsIgnoreCase(value) || type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }

    Тоді працюватиме і "PART_TIME", і "Part time".


📌 Висновок:

    Твій варіант повністю правильний і best practice для зовнішнього API.
    Якщо треба підтримати обидва варіанти (name + displayName) → дописуємо умову в @JsonCreator.
 */

/*
‼️ @JsonValue і @JsonCreator не мають прямого відношення до того, чи ми самі викликаємо ObjectMapper, чи ні.

🔹 Як воно працює насправді

    Spring Boot під капотом усе одно використовує ObjectMapper Jackson для серіалізації/десеріалізації.
    Коли ти робиш @RestController і приймаєш/повертаєш DTO — це той самий процес, що і якби ти сам викликав mapper.readValue(...) чи mapper.writeValueAsString(...).

    Різниця лише в тому, що в контролерах цим керує Spring автоматично.

👉 Тобто @JsonValue і @JsonCreator впливають завжди, незалежно від того, чи ти вручну працюєш з ObjectMapper, чи воно відбувається "за кулісами".


🔹 Коли потрібні @JsonValue і @JsonCreator

    Якщо тебе влаштовує стандартна поведінка (enum → name) → нічого не треба, буде "PART_TIME".
    Якщо ти хочеш інше представлення (наприклад, displayName = "Part time") → тоді додаєш @JsonValue і @JsonCreator.

    Вони потрібні як у випадку:
        коли ти тестуєш через ObjectMapper вручну,
        так і коли Spring серіалізує/десеріалізує DTO автоматично.

🔹 Простий приклад
    enum EmploymentType {
        FULL_TIME("Full time"),
        PART_TIME("Part time");

        private final String displayName;

        EmploymentType(String displayName) { this.displayName = displayName; }

        @JsonValue
        public String toJson() {
            return displayName; // замість "PART_TIME" буде "Part time"
        }

        @JsonCreator
        public static EmploymentType fromJson(String value) {
            for (EmploymentType t : values()) {
                if (t.displayName.equalsIgnoreCase(value)) {
                    return t;
                }
            }
            throw new IllegalArgumentException("Unknown value: " + value);
        }
    }


    Тепер:
        POST запит з "employmentType": "Part time" → десеріалізується у EmploymentType.PART_TIME.
        GET відповідь із DTO → серіалізується у "employmentType": "Part time".

        І тут нам не треба напряму чіпати ObjectMapper — Spring Boot все зробить за нас.


📌 Висновок

    @JsonValue / @JsonCreator не "для ObjectMapper вручну".
    Вони впливають на будь-яку серіалізацію/десеріалізацію Jackson → і в контролерах, і в сервісах, і в тестах.
    Ми пишемо їх тільки тоді, коли хочемо кастомне представлення enum у JSON (не name(), а щось своє).



🔹 1. Вхідний запит (Request DTO)

    Коли клієнт (фронт чи інший сервіс) шле нам JSON, Spring Boot через Jackson автоматично перетворює його в DTO.

    Якщо DTO має поле типу enum, то Jackson очікує значення у форматі name() (за замовчуванням).

    Приклад JSON:
        {
          "name": "Nazar",
          "role": "USER",
          "employmentType": "PART_TIME"
        }

    DTO:
        record EmployeeRequestDto(String name, Role role, EmploymentType employmentType) {}


    👉 Тут головна перевага: безпечний тип.
        Якщо прилетить щось зайве ("employmentType":"SOME_UNKNOWN") → Jackson кине помилку.
        Тобто ми автоматично захищені від сміття у JSON.


🔹 2. Усередині коду (сервіс, бізнес-логіка)

    Ми працюємо з типобезпечним enum:
    if (dto.employmentType() == EmploymentType.FULL_TIME) { ... }

    Тут уже взагалі неважливо, як воно прийшло у JSON → ми працюємо з enum як зі звичайним типом.


🔹 3. Вихідний респонс (Response DTO)

    Коли ми повертаємо DTO у контролері, Spring Boot через Jackson знову автоматично серіалізує його в JSON.
    За замовчуванням enum → name() (PART_TIME).
    Але якщо треба для UI дружні назви → можна додати @JsonValue.

    Приклад:
        record EmployeeResponseDto(String name, Role role, EmploymentType employmentType) {}

    Вихідний JSON:
        {
          "name": "Nazar",
          "role": "USER",
          "employmentType": "PART_TIME"
        }

🔹 4. Чи треба нам вручну юзати ObjectMapper?

    ❌ У 99% випадків — ні.
        Вхідні JSON → DTO десеріалізуються автоматично (Jackson інтегрований у Spring Boot).
        Вихідні DTO → JSON серіалізуються автоматично.

    ✅ Ми можемо юзати ObjectMapper вручну тільки:
        у тестах (наприклад, коли перевіряємо JSON-відповідь),
        у рідкісних випадках, якщо треба серіалізувати у файл чи в Kafka message.


📌 Висновок
    Request DTO → enum як name() (безпечний, якщо не валідний — буде помилка).
    Service Layer → працюємо з enum як з нормальним типом.
    Response DTO → за замовчуванням віддаємо name(). Якщо треба дружнє — ставимо @JsonValue.
    ObjectMapper → не юзаємо напряму у звичайному коді, все працює “за кулісами” Spring Boot.
 */

/*
🔹 Як поводиться Jackson за замовчуванням
    enum Role {
        ADMIN,
        USER,
        DRIVER
    }

    Приклад DTO:
        record EmployeeDto(String name, Role role) {}

    Вхідний JSON (Request):
        {
          "name": "Nazar",
          "role": "USER"
        }

    Вихідний JSON (Response):
        {
          "name": "Nazar",
          "role": "USER"
        }


👉 Тут Jackson просто серіалізує/десеріалізує за name().

🔹 Чи треба @JsonValue / @JsonCreator?
    ❌ Ні, не треба.
    Бо у enum немає додаткових полів (типу displayName), отже достатньо name().
    Це і є best practice: зберігати у JSON значення як "ADMIN", "USER", "DRIVER".

🔹 Коли все ж можна втрутитися?

    Якщо ти хочеш, щоб JSON показував не name(), а щось інше.
    Наприклад, замість "USER" → "User":

    enum Role {
        ADMIN, USER, DRIVER;

        @Override
        public String toString() {
            return name().charAt(0) + name().substring(1).toLowerCase();
        }
    }

    Або з @JsonValue:
        enum Role {
            ADMIN, USER, DRIVER;

            @JsonValue
            public String toJson() {
                return name().toLowerCase(); // "admin", "user", "driver"
            }
        }

    Тоді JSON виглядатиме ось так:
        {
          "name": "Nazar",
          "role": "user"
        }


📌 Висновок
    Якщо enum простий (без полів) → нічого додаткового не треба, Jackson серіалізує як name().
    @JsonValue / @JsonCreator потрібні тільки коли ти хочеш змінити формат JSON (наприклад, "user" замість "USER" або "User").
    У 90% випадків для простих enum → просто лишаємо як є.
 */