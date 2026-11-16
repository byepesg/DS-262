public class SampleData
{
    public static RegistrationSystem create() {
        RegistrationSystem system = new RegistrationSystem();
        system.addGroup(group("1000004", "Cálculo Diferencial", "Gr-1", 2, "LUN", 7, 9));
        system.addGroup(group("1000004", "Cálculo Diferencial", "Gr-2", 1, "MAR", 9, 11));
        system.addGroup(group("1000004", "Cálculo Diferencial", "Gr-3", 3, "JUE", 14, 16));
        system.addGroup(group("1000003", "Álgebra Lineal", "Gr-1", 3, "LUN", 8, 10));
        system.addGroup(group("1000003", "Álgebra Lineal", "Gr-2", 2, "VIE", 7, 9));
        system.addStudent(new Student("2024101", "Ana",    true,  3.8, false, 40.0, 0.0));
        system.addStudent(new Student("2024102", "Carla",  false, 4.1, false, 92.0, 0.0));
        system.addStudent(new Student("2024103", "Daniel", false, 3.9, false, 85.0, 0.0));
        system.addStudent(new Student("2024104", "Elena",  false, 4.3, false, 55.0, 7.5));
        system.addStudent(new Student("2024105", "Felipe", false, 4.0, false, 35.0, 6.0));
        return system;
    }

    private static CourseGroup group(String code, String name, String number, int capacity,
                                     String day, int start, int end) {
        CourseGroup group = new CourseGroup(code, name, number, capacity);
        group.addTimeSlot(new TimeSlot(day, start, end));
        return group;
    }
}
