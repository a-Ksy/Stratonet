package Stratonet.Infrastructure.Services.Query;

import Stratonet.Core.Enums.APIType;
import Stratonet.Core.Services.Query.IQueryService;

public class QueryService implements IQueryService
{
    public boolean validateAPIType(String apiTypeAsString)
    {
        try
        {
            APIType apiType = APIType.valueOf(apiTypeAsString);
            return true;
        }
        catch (IllegalArgumentException ex)
        {
            return false;
        }
    }
}
