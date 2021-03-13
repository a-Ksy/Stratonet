package Stratonet.Core.Services.Save;

public interface ISaveService
{
    void SaveObjectAsJSON(Object object, String fileName);

    boolean SaveImageFromByteArray(byte[] response, String filename);

    void DeleteImage(String fileName);
}
