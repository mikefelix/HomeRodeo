This is a rewrite of the Android frontend to the home automation server that collects and presents the various devices in my home, including:
- Light switches
- Fermenter for fermenting various fermentables
- Weather display for inside (via thermostat) and outside (via weather data API) air conditions
- Alarm clock, which is a Raspberry Pi hooked to a speaker, hiding in my wall :)

This is something I actually use, but it's also a playground and showcase. The ongoing rewrite is my attempt to put into practice some lessons learned at my previous Android job.
- Better event architecture (state- rather than event-based)
- Better navigation using up-to-date practices
- Several different testing types
- Eventually I'll experiment with a more Clean & MVI approach.
