package org.example.paginarefiltraredb.domain.entities;

public enum CarStatus {
    NEW,            // Mașina există în sistem, dar nu a fost trimisă spre aprobare
    NEEDS_APPROVAL, // Dealer-ul a trimis mașina spre aprobare
    REJECTED,       // Cererea a fost respinsă de admin
    APPROVED        // Mașina este aprobată de admin
}
