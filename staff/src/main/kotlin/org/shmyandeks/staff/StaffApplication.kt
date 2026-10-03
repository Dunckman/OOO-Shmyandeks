package org.shmyandeks.staff

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class StaffApplication

fun main(args: Array<String>) {
    runApplication<StaffApplication>(*args)
}
