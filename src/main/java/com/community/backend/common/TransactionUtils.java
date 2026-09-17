package com.community.backend.common;

import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public final class TransactionUtils {

    private TransactionUtils() {
    }

    // 把动作推迟到事务提交之后执行；不在事务里时立即执行。
    // 删缓存、发消息都必须等提交后再做，否则别人可能读到还没提交的旧数据
    public static void afterCommit(Runnable action) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    action.run();
                }
            });
        } else {
            action.run();
        }
    }
}
