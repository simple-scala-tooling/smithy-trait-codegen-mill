package org.scala.abusers

import mill._
import mill.scalalib._
import os._

object SmithyTraitCodegenPlugin extends mill.define.ExternalModule {
  def millDiscover = mill.define.Discover[this.type]

  trait SmithyTraitCodegenSettings extends ScalaModule {
    def smithyTraitCodegenSourcesDir: T[PathRef]
    def smithyTraitCodegenDependencies: T[Seq[os.Path]] = T(Seq.empty[os.Path])
    def smithyTraitCodegenJavaPackage: T[String]
    def smithyTraitCodegenNamespace: T[String]

    def smithyTraitCodegenGenerateSmithy = T.task {
      val args = org.scala.abusers.Args(
        targetDir = T.dest,
        smithySourcesDir = smithyTraitCodegenSourcesDir().path,
        dependencies = smithyTraitCodegenDependencies(),
        targetPackage = smithyTraitCodegenJavaPackage(),
        sourceNamespace = smithyTraitCodegenNamespace()
      )
      val output = org.scala.abusers.SmithyTraitCodegenImpl
        .generate(args)
      output
    }

    override def generatedSources = T.task {
      val output = smithyTraitCodegenGenerateSmithy()
      super.generatedSources() ++ Seq(PathRef(output.javaDir))
    }

    override def resources: T[Seq[PathRef]] = T {
      val output = smithyTraitCodegenGenerateSmithy()
      super.resources() ++ Seq(
        smithyTraitCodegenSourcesDir(),
        PathRef(output.metaDir)
      )
    }
  }
}
