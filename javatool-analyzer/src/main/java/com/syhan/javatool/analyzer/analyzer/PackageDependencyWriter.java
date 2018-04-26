package com.syhan.javatool.analyzer.analyzer;

import com.syhan.javatool.analyzer.store.JavaDependencyStore;
import com.syhan.javatool.share.data.Pair;

import java.util.ArrayList;
import java.util.List;

public class PackageDependencyWriter {
    //
    private String fromModule;
    private String toModule;
    private JavaDependencyStore store;

    public PackageDependencyWriter(String fromModule, String toModule, JavaDependencyStore store) {
        //
        this.fromModule = fromModule;
        this.toModule = toModule;
        this.store = store;
    }

    public void write() {
        //
        List<Pair<String, Integer>> fromPackages = extractPackages(fromModule);
        List<Pair<String, Integer>> toPackages = extractPackages(toModule);
        for (Pair fromPackage : fromPackages) {
            write(fromPackage, toPackages);
        }
    }

    private void write(Pair<String, Integer> fromPackage, List<Pair<String, Integer>> toPackages) {
        //
        for (Pair toPackage: toPackages) {
            write(fromPackage, toPackage);
        }
    }

    private void write(Pair<String, Integer> fromPackage, Pair<String, Integer> toPackage) {
        //
        if (!store.exists(fromPackage.x, toPackage.x)) {
            store.create(fromPackage.x, fromPackage.y, toPackage.x, toPackage.y);
        }
    }

    // com.foo.bar.Sample -> com, com.foo, com.foo.bar, com.foo.bar.Sample
    private List<Pair<String, Integer>> extractPackages(String moduleName) {
        //
        String[] packs = moduleName.split("\\.");
        StringBuilder sb = new StringBuilder();
        List<Pair<String, Integer>> packages = new ArrayList<>();
        for (int i = 0; i < packs.length; i++) {
            if (i > 0) {
                sb.append(".");
            }
            sb.append(packs[i]);
            packages.add(new Pair<>(sb.toString(), i + 1));
        }

        return packages;
    }
}
