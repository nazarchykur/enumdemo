package org.example.enumdemo.withjpa;

import jakarta.persistence.*;

@Entity
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔹 Варіант 1: enum без поля → зберігаємо як STRING
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // 🔹 Варіант 2: enum з полем + кастомне значення
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmploymentType employmentType;

    // стандартні гетери/сетери, конструктори
    public Employee() {}

    public Employee(Role role, EmploymentType employmentType) {
        this.role = role;
        this.employmentType = employmentType;
    }

    public Long getId() { return id; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public EmploymentType getEmploymentType() { return employmentType; }
    public void setEmploymentType(EmploymentType employmentType) { this.employmentType = employmentType; }
}

/**
 * Enum без додаткових полів.
 * ✅ Зберігається в БД як STRING → "ADMIN", "USER", "DRIVER".
 */
enum Role {
    ADMIN,
    USER,
    DRIVER
}

/**
 * Enum з кастомним представленням (CamelCase → TitleCase).
 * ✅ Для UI чи інтеграцій може бути зручніше, ніж сирі імена.
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

    // ⚠️ Важливо: JPA за замовчуванням буде зберігати name() → FULL_TIME, PART_TIME, CONTRACTOR
    // Якщо треба зберігати displayName у БД → тоді треба кастомний @Converter
}


/*
📘 Теоретичне пояснення

🔹 Як JPA працює з enum?

    Якщо ми додаємо поле типу enum в entity, JPA має два режими:
        1. @Enumerated(EnumType.ORDINAL) → зберігається індекс (0, 1, 2).
            ⚠️ Небезпечно: якщо зміниться порядок у enum → всі дані "зламаються".

        2. @Enumerated(EnumType.STRING) → зберігається ім’я (name()) константи → "ADMIN", "USER".
            ✅ Best practice: безпечніше, зрозуміліше, легко читати напряму з БД.


🔹 Enum без поля (Role)
    У БД буде збережено "ADMIN", "USER", "DRIVER".
    Використовуємо, коли нам достатньо технічної назви.
    Це найчастіший випадок: ролі, статуси, прості довідники.

🔹 Enum з полем (EmploymentType)
    Має displayName (наприклад, "Full time").
    Використовуємо для UI/репортів, коли треба показувати красиве ім’я.
    У БД все одно збережеться FULL_TIME (бо JPA бере name()).
    Якщо ми хочемо зберігати "Full time" → треба конвертер (@Converter), який каже JPA як писати/читати значення.

🔹 Чому найчастіше зберігаємо STRING?
    Людиночитабельність → у БД легко зрозуміти значення без довідників.
    Безпека → порядок констант можна міняти, і дані не зламаються.
    Інтеграції → легше мапити enum у JSON, CSV, API.

📊 Приклад даних у БД
    CREATE TABLE employees (
        id BIGSERIAL PRIMARY KEY,
        role VARCHAR(50) NOT NULL,
        employment_type VARCHAR(50) NOT NULL
    );

    -- Збереження прикладу:
        INSERT INTO employees (role, employment_type) VALUES ('ADMIN', 'FULL_TIME');
        INSERT INTO employees (role, employment_type) VALUES ('DRIVER', 'PART_TIME');

    -- У БД ми побачимо:
        id | role   | employment_type
        ---+--------+-----------------
        1  | ADMIN  | FULL_TIME
        2  | DRIVER | PART_TIME



📌 Висновки
    1. Enum без полів → прості випадки (роль, статус).
        - Зберігаємо як STRING → "ADMIN", "USER".
        - Найбезпечніший варіант.

    2. Enum з полями (displayName, code) → коли потрібне дружнє ім’я чи додаткові властивості.
        - У БД все одно зберігається name().
        - Для збереження кастомного значення → потрібен @Converter.

    3. Ordinals (0,1,2) → майже ніколи не використовуємо (ризик зламаних даних).
 */
