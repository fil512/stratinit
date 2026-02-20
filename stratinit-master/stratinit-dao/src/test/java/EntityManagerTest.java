import com.kenstevens.stratinit.StratInitTest;
import org.hibernate.internal.SessionImpl;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class EntityManagerTest extends StratInitTest {
    @PersistenceContext
    private EntityManager entityManager;

    @Test
    public void testRunningUnderH2() {
        SessionImpl session = (SessionImpl) entityManager.getDelegate();
        assertTrue(session.getFactory().getJdbcServices().getDialect() instanceof org.hibernate.dialect.H2Dialect, "RUNNING IN H2");
    }
}
