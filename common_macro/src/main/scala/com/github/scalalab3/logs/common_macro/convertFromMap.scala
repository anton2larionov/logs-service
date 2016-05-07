package com.github.scalalab3.logs.common_macro

import java.util

import scala.language.experimental.macros
import scala.reflect.macros.whitebox

trait FromMap[T] {
  implicit class HashMapExt(x: util.HashMap[String, Any]) {
    def safeGet(k: String) = Option(x.get(k))
  }

  def fromMap(map: util.HashMap[String, Any]): Option[T]
}

object FromMap {

  implicit def materializeMappable[T]: FromMap[T] = macro materializeMappableImpl[T]

  def materializeMappableImpl[T: c.WeakTypeTag](c: whitebox.Context):
      c.Expr[FromMap[T]] = {

    import c.universe._
    val tpe = weakTypeOf[T]

    // check if case class passed
    if (!(tpe.typeSymbol.isClass && tpe.typeSymbol.asClass.isCaseClass)) {
      c.abort(c.enclosingPosition, "Not a case class")
    }

    val companion = tpe.typeSymbol.companion

    val fields = tpe.decls.collectFirst {
      case m: MethodSymbol if m.isPrimaryConstructor => m
    }.get.paramLists.head

    val names = fields.map { field =>
      val name = field.name.toTermName
      q"$name"
    }

    val forLoop = fields.map { field =>
      val name = field.name.toTermName
      val decoded = name.decodedName.toString
      val returnType = tpe.decl(name).typeSignature

      val ret = decoded match {
        case "id" => q"Option(map.safeGet($decoded))"
        case _ => q"map.safeGet($decoded)"
      }
      fq"$name <- $ret.map(_.asInstanceOf[$returnType])"
    }

    c.Expr[FromMap[T]] {
      q"""
      new FromMap[$tpe] {
        def fromMap(map: java.util.HashMap[String, Any]): Option[$tpe] = {
          for (..$forLoop) yield $companion(..$names)
        }
      }
    """
    }
  }
}