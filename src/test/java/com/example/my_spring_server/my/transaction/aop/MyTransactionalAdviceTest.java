package com.example.my_spring_server.my.transaction.aop;

import com.example.my_spring_server.my.transaction.MyTransactionManager;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MyTransactionalAdviceTest {
    @Mock private MyTransactionManager transactionManager;
    @Mock private MethodInvocation invocation;

    @InjectMocks private MyTransactionalAdvice advice;

    @Test
    @DisplayName("정상 실행되면 커밋된다")
    void commitOnSuccess() throws Throwable {
        // Given
        Object expected = "Success Result";
        when(invocation.proceed()).thenReturn(expected);

        // When
        Object actual = advice.invoke(invocation);

        // Then
        assertEquals(expected, actual);
        verify(transactionManager).startTransaction();
        verify(transactionManager).commit();

        verify(transactionManager, never()).rollback(); // 롤백은 호출 X
    }

    @Test
    @DisplayName("예외가 발생하면 롤백하고 예외를 던진다")
    void rollbackOnException() throws Throwable {
        // Given
        RuntimeException expectedException = new RuntimeException("작업 중 예외");
        when(invocation.proceed()).thenThrow(expectedException);

        // When & Then
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            advice.invoke(invocation);
        });

        assertEquals(expectedException, thrown);
        verify(transactionManager).startTransaction();
        verify(transactionManager).rollback();

        verify(transactionManager, never()).commit(); // 커밋은 호출 X
    }

}