package com.group_7.library_management.repository;

import java.time.Instant;

public interface AdminHistoryProjection {
    String getEventId();
    String getAction();
    String getUserName();
    String getBookTitle();
    String getReferenceCode();
    Instant getEventAt();
}
