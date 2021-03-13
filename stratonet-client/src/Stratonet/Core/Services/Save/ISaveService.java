package Stratonet.Core.Services.Save;

public interface ISaveService
{
    void SaveObjectAsJSON(Object object, String fileName);

    void SaveImageFromByteArray(byte[] response, String filename);

    void DeleteImage(String fileName);
}
