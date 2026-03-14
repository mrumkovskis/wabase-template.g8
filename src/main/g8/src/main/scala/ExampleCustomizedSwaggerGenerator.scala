package $package_name$

import io.swagger.v3.oas.models.media.NumberSchema
import org.wabase.swagger.WabaseSwaggerGenerator
import org.wabase.{WabaseRequestContext, WabaseSwaggerGeneratorFactory}
import org.wabase.WabaseSwaggerGeneratorFactory.WabaseDefaultSwaggerGenerator


class ExampleCustomizedSwaggerGenerator(ctx: WabaseRequestContext) extends WabaseDefaultSwaggerGenerator(ctx) {
  override def schemaFromType(type_ : org.mojoz.metadata.Type) = type_.name match {
    case "example_euro_custom_type" => new NumberSchema
    case _ => super.schemaFromType(type_)
  }
}

object ExampleSwaggerGeneratorFactory extends WabaseSwaggerGeneratorFactory {
  override def createSwaggerGenerator(ctx: WabaseRequestContext): WabaseSwaggerGenerator = new ExampleCustomizedSwaggerGenerator(ctx)
}
