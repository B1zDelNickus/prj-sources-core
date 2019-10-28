package codes.spectrum.sources.core.source.systems

import codes.spectrum.sources.core.SourceDefinition
import codes.spectrum.sources.core.source.RestSystem

object DevRestSystem : RestSystem(
    name = "Стейдж",
    url = SourceDefinition.Instance.devHostName,
    index = 1
)