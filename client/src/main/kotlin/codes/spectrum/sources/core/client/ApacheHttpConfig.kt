package codes.spectrum.sources.core.client

data class ApacheHttpConfig(
        /**
         * Apache embedded http redirect, default = false. Obsolete by `HttpRedirect` feature.
         * It uses the default number of redirects defined by Apache's HttpClient that is 50.
         */
        val followRedirects: Boolean = false,

        /**
         * Timeouts.
         * Use `0` to specify infinite.
         * Negative value mean to use the system's default value.
         */

        /**
         * Max time between TCP packets - default 10 seconds.
         */
        val socketTimeout: Int = 10_000,
        /**
         * Max time to establish an HTTP connection - default 10 seconds.
         */
        val connectTimeout: Int = 10_000,
        /**
         * Max time for the connection manager to start a request - 20 seconds.
         */
        val connectionRequestTimeout: Int = 20_000
)