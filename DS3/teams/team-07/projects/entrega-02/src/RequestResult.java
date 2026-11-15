/** Resultado de una solicitud de inscripción a un grupo. */
public enum RequestResult
{
    /** Había cupo y el estudiante quedó inscrito. */
    ENROLLED,
    /** No había cupo; el estudiante aceptó la asignación automática y quedó en lista de espera. */
    WAITLISTED,
    /** No había cupo y el estudiante no aceptó la asignación automática. */
    REJECTED
}
