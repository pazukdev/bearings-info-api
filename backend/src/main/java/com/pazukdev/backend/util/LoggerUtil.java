package com.pazukdev.backend.util;

import com.pazukdev.backend.entity.UserAction;
import com.pazukdev.backend.entity.UserEntity;
import com.pazukdev.backend.entity.abstraction.AbstractEntity;
import com.pazukdev.backend.repository.UserActionRepository;
import com.pazukdev.backend.service.EmailSenderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.pazukdev.backend.util.UserActionUtil.ActionType;

/**
 * @author Siarhei Sviarkaltsau
 */
public class LoggerUtil {

    public final static Logger LOGGER = LoggerFactory.getLogger(LoggerUtil.class);

    public static void info(final String message) {
        LOGGER.info(message);
    }

    public static void warn(final String message) {
        LOGGER.warn(message);
    }

    public static void error(final String message) {
        LOGGER.error(message);
    }

    public static void warn(@Nonnull final UserAction action,
                            @Nonnull final UserActionRepository repo,
                            @Nullable final AbstractEntity entity,
                            @Nullable final UserEntity user,
                            @Nullable final EmailSenderService service) {
        warn(new ArrayList<>(Collections.singletonList(action)), repo, entity, user, service);

    }

    public static void warn(@Nonnull final List<UserAction> actions,
                            @Nonnull final UserActionRepository repo,
                            @Nullable final AbstractEntity entity,
                            @Nullable final UserEntity user,
                            @Nullable final EmailSenderService service) {

        actions.removeIf(Objects::isNull);

        final String subject;
        if (entity != null && user != null) {
            final String whatHappened;
            if (actions.size() == 1 && !actions.get(0).getActionType().equals(ActionType.ADD)) {
                whatHappened = actions.get(0).getActionType() + "d";
            } else {
                whatHappened = "changed";
            }
            subject = entity + " " + whatHappened + " by " + user.getName();
        } else {
            subject = "items management";
        }
        boolean printStartAndEnd = actions.size() > 0;

        if (printStartAndEnd) {
            warn("--- report start ---");
        }
        warn(subject);
        actions.forEach(action -> {
            if (action == null) {
                warn(null);
            } else {
                warn(action.getMessage());
            }
        });
        if (printStartAndEnd) {
            warn("--- report end ---");
        }

        for (final UserAction action : actions) {
            repo.save(action);
        }
        if (service != null && !UserUtil.isSuperAdmin(user)) {
            final String text = toString(actions);
            service.emailToYourself(subject, text);
        }
    }

    private static String toString(final List<UserAction> actions) {
        String result = "";
        for (final UserAction action : actions) {
            result += action.getMessage() + "\n";
        }
        return result;
    }

}
