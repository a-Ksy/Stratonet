package Runnable;

import Stratonet.Infrastructure.Services.Insight.InsightService;
import Stratonet.Infrastructure.Services.Startup.StartupService;

public class Main
{
    public static void main(String[] args)
    {
        //StartupService startupService = new StartupService();
        InsightService insightService = new InsightService();
        insightService.GetInsightWeather();
    }
}
