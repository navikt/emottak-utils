package no.nav.emottak.utils.coroutines

import arrow.fx.coroutines.resourceScope
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlin.coroutines.cancellation.CancellationException

class ResourceScopeSpec : StringSpec(
    {
        "create coroutineScope as a resource" {
            resourceScope {
                val parentJob = coroutineContext[Job]
                val parentDispatcher = coroutineContext[CoroutineDispatcher]

                val scope = coroutineScope(coroutineContext)

                scope.shouldBeInstanceOf<CoroutineScope>()

                val scopeJob = scope.coroutineContext[Job]
                val scopeDispatcher = scope.coroutineContext[CoroutineDispatcher]

                scopeJob shouldNotBe null
                parentJob shouldNotBe null

                scopeJob!!.isActive shouldBe true
                parentJob!!.isActive shouldBe true

                scopeDispatcher shouldBe parentDispatcher

                val deferred = scope.async { "async" }

                scopeJob.children shouldNotBe emptySequence<Job>()

                deferred.await() shouldBe "async"

                scopeJob.cancel()

                scopeJob.isActive shouldBe false
                deferred.isActive shouldBe false
            }
        }

        "coroutineScope completes successfully and cancels job" {
            resourceScope {
                val scope = coroutineScope(coroutineContext)
                val job = scope.coroutineContext[Job]

                scope.cancel()
                job!!.join()
                job.isCancelled shouldBe true
            }
        }

        "coroutineScope cancels job when cancelled externally" {
            resourceScope {
                val scope = coroutineScope(coroutineContext)
                val job = scope.coroutineContext[Job]

                val exception = CancellationException("Test Cancellation")
                scope.cancel(exception)

                job!!.join()
                job.isCancelled shouldBe true
                job.getCancellationException().message shouldBe "Test Cancellation"
            }
        }

        "coroutineScope cancels job with failure" {
            resourceScope {
                val scope = coroutineScope(coroutineContext)
                val job = scope.coroutineContext[Job]

                val failure = RuntimeException("BOOM")
                val exception = CancellationException(failure)
                scope.cancel(exception)

                job!!.join()
                job.isCancelled shouldBe true
                job.getCancellationException().cause shouldBe failure
            }
        }
    }
)
