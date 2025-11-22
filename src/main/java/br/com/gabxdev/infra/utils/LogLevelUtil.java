package br.com.gabxdev.infra.utils;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;

@Slf4j
public class LogLevelUtil {

    /**
     * Atualiza o log level global (ROOT).
     *
     * @param level ex: "TRACE", "DEBUG", "INFO", "WARN", "ERROR", "OFF"
     */
    public static void updateRootLogLevel(String level) {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        ch.qos.logback.classic.Logger rootLogger = context.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);

        Level newLevel = Level.valueOf(level.toUpperCase());

        if (rootLogger.getLevel() == newLevel) {
            return;
        }

        log.info("Decteada mudança na configuração de log: {} -> {}", rootLogger.getLevel().levelStr, newLevel.levelStr);

        rootLogger.setLevel(newLevel);
    }
}
