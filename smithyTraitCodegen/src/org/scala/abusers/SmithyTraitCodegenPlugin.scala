package org.scala.abusers

import mill.*
import mill.scalalib.*

object SmithyTraitCodegenPlugin extends mill.api.ExternalModule {
  def millDiscover = mill.api.Discover[this.type]

  trait SmithyTraitCodegenSettings extends ScalaModule {
    def smithyTraitCodegenSourcesDir: T[PathRef]
    def smithyTraitCodegenDependencies: T[Seq[os.Path]] = Task(Seq.empty[os.Path])
    def smithyTraitCodegenJavaPackage: T[String]
    def smithyTraitCodegenNamespace: T[String]

    def smithyTraitCodegenGenerateSmithy = Task.Anon {
      val args = org.scala.abusers.Args(
        targetDir = Task.dest,
        smithySourcesDir = smithyTraitCodegenSourcesDir().path,
        dependencies = smithyTraitCodegenDependencies(),
        targetPackage = smithyTraitCodegenJavaPackage(),
        sourceNamespace = smithyTraitCodegenNamespace()
      )
      val output = org.scala.abusers.SmithyTraitCodegenImpl
        .generate(args)
      output
    }

    override def generatedSources = Task {
      val output = smithyTraitCodegenGenerateSmithy()
      super.generatedSources() ++ Seq(PathRef(output.javaDir))
    }

    override def resources: T[Seq[PathRef]] = Task {
      val output = smithyTraitCodegenGenerateSmithy()
      super.resources() ++ Seq(
        smithyTraitCodegenSourcesDir(),
        PathRef(output.metaDir)
      )
    }
  }
}
