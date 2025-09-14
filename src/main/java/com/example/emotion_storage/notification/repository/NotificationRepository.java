package com.example.emotion_storage.notification.repository;

import com.example.emotion_storage.notification.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.isRead = false AND n.deletedAt IS NULL")
    List<Notification> findUnreadNotificationsByUserId(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user.id = :userId AND n.isRead = false AND n.deletedAt IS NULL")
    Long countUnreadNotificationsByUserId(@Param("userId") Long userId);
}
