package kmp.android.shared.extension

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.daysUntil
import kotlinx.datetime.toJavaLocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

val LocalDate.daysUntil: Int
    get() = Clock.System.now()
        .daysUntil(
            other = this.atStartOfDayIn(TimeZone.currentSystemDefault()),
            timeZone = TimeZone.currentSystemDefault()
        ) + 1


val LocalDate.localizedString: String
    get() = this.toJavaLocalDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))