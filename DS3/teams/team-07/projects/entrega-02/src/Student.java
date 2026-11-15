/**
 * Estudiante de pregrado con los datos que definen su prioridad de inscripción
 * según la Circular 13 de 2026.
 *
 * El IPA es un dato de entrada: la universidad ya lo calcula, así que el
 * sistema no lo recalcula.
 *
 * Orden de prioridad (compareTo): a.compareTo(b) < 0 significa que a tiene
 * MÁS prioridad que b.
 */
public class Student implements Comparable<Student>
{
    private String code;
    private String name;

    private boolean hasDisability;               // Grupo 1
    private double papa;                         // Grupo 2 (PAPA <= 3.1) y desempates
    private boolean academicVulnerability;       // Grupo 2 (PEAMA, riesgo, etc.)
    private double curricularProgress;           // Porcentaje de avance (ej: 85.0 para 85%)
    private double ipa;                          // Índice de Prioridad de Asignación (dato oficial, solo Grupo 4)

    public Student(String code, String name, boolean hasDisability, double papa,
                   boolean academicVulnerability, double curricularProgress, double ipa) {
        this.code = code;
        this.name = name;
        this.hasDisability = hasDisability;
        this.papa = papa;
        this.academicVulnerability = academicVulnerability;
        this.curricularProgress = curricularProgress;
        this.ipa = ipa;
    }

    // Grupo 1: Discapacidad
    // Grupo 2: Vulnerabilidad académica (PAPA <= 3.1 o PEAMA)
    // Grupo 3: Avance curricular >= 80%
    // Grupo 4: Avance curricular < 80%
    public int getAssignedGroup() {
        if (hasDisability) return 1;
        if (academicVulnerability || papa <= 3.1) return 2;
        if (curricularProgress >= 80.0) return 3;
        return 4;
    }

    /**
     * Compara por grupo (1 antes que 4) y, dentro del grupo:
     * G1 PAPA mayor primero; G2 PAPA menor primero (más riesgo);
     * G3 avance mayor primero y luego PAPA mayor; G4 IPA mayor primero.
     * Último desempate: código, para que el orden sea total. O(1).
     */
    @Override
    public int compareTo(Student other) {
        int byGroup = Integer.compare(getAssignedGroup(), other.getAssignedGroup());
        if (byGroup != 0) return byGroup;

        int byScore;
        switch (getAssignedGroup()) {
            case 1:
                byScore = Double.compare(other.papa, papa);
                break;
            case 2:
                byScore = Double.compare(papa, other.papa);
                break;
            case 3:
                byScore = Double.compare(other.curricularProgress, curricularProgress);
                if (byScore == 0) byScore = Double.compare(other.papa, papa);
                break;
            default:
                byScore = Double.compare(other.ipa, ipa);
        }
        if (byScore != 0) return byScore;
        return code.compareTo(other.code);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student)) return false;
        return code.equals(((Student) o).code);
    }

    @Override
    public int hashCode() {
        return code.hashCode();
    }

    @Override
    public String toString() {
        return name + " (G" + getAssignedGroup() + ")";
    }

    public String getCode() {
        return code;
    }
    public String getName() {
        return name;
    }
    public double getCurricularProgress() {
        return curricularProgress;
    }
    public double getPapa() {
        return papa;
    }
    public double getIpa() {
        return ipa;
    }
}
