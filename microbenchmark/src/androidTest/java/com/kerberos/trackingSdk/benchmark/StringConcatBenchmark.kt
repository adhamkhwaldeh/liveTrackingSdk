package com.kerberos.trackingSdk.benchmark

import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StringConcatBenchmark {

    @get:Rule
    val benchmarkRule = BenchmarkRule()

    @Test
    fun stringBuilderConcat() {
        benchmarkRule.measureRepeated {
            val result = StringBuilder()
            for (i in 0 until 1_000) {
                result.append(i)
            }
            result.toString()
        }
    }
}
