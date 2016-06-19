package eu.toolchain.ogt;

public interface Encoding<Target, Source> {
    Target encode(Source instance);

    Source decode(Target instance);
}
