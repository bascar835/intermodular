package com.example.experiencias.scheduling;

import java.sql.Connection;
import java.time.LocalDateTime;

import javax.sql.DataSource;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.experiencias.repository.CheckoutPreviewRepository;

/**
 * Tarea programada que elimina automáticamente los checkout previews
 * que llevan más de 1 hora sin confirmar.
 *
 * Se ejecuta cada 10 minutos en segundo plano, sin necesidad de
 * ninguna petición HTTP.
 */
@Component
public class CheckoutPreviewCleanupTask {

    private final DataSource ds;

    public CheckoutPreviewCleanupTask(DataSource ds) {
        this.ds = ds;
    }

    // Ejecutar cada 10 minutos
    @Scheduled(fixedRate = 10 * 60 * 1000)
    public void cleanup() {
        try (Connection con = ds.getConnection()) {
            CheckoutPreviewRepository repo = new CheckoutPreviewRepository(con);
            LocalDateTime limite = LocalDateTime.now().minusHours(1);
            int deleted = repo.deleteOlderThan(limite);
            if (deleted > 0) {
                System.out.println("[Cleanup] Checkout previews caducados eliminados: " + deleted);
            }
        } catch (Exception e) {
            System.err.println("[Cleanup] Error al eliminar previews caducados: " + e.getMessage());
        }
    }
}
