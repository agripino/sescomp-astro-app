# sescomp-astro-app

## Requisitos
### NASA World Wind
https://github.com/NASAWorldWind/WorldWindJava/releases (worldwind-v2.1.0.zip)

Na IntelliJ IDEA, em File > Project Structure > Project Setttings > Libraries, adicionar

gdal.jar
gluegen-rt.jar
jogl-all.jar
worldwind.jar
worldwindx.jar

Esses arquivos estão no .zip baixado do link acima.

### Python
Para executar o script download_tle_files.py e atualizar os TLEs.

E o pacote tqdm (usado no script acima):

pip install tqdm
