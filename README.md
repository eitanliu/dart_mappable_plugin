# DartMappable(Json To DartMappable Or JsonSerializable)

## How to use

1. GitHub [Releases](https://github.com/eitanliu/dart_mappable_plugin/releases) or IntelliJ IDEs Plugin Marketplace [DartMappable](https://plugins.jetbrains.com/plugin/21845) page download plugin
2. Open in the IDE `Setting` -> `Plugins`, right click setting icon ⚙️ -> `Install Plugin from Disk...` selected you download file.

<!-- Plugin description -->
Json to dart data classes are provided, and dart files ending in entity are provided to generate dart class factory for use.  

Right click on package -> `New` -> `Json To DartMappable`　And Then you will know how to use.  

If you change the fields in the class, right click on package -> `Flutter Command` -> `Flutter Run Build Runner`, shortcut key is changed to `Alt + r`.  

### 1. Use dart_mappable

Don't forget to set up `dart_mappable` into your project : https://pub.dev/packages/dart_mappable  
Add into your `pubspec.yaml`  
```yaml
dependencies:
  dart_mappable: ^3.0.0  
dev_dependencies: 
  build_runner: ^2.1.0
  dart_mappable_builder: ^3.0.2
```
Or Run Terminal  
```shell
flutter pub add dart_mappable
flutter pub add build_runner --dev
flutter pub add dart_mappable_builder --dev
```

### 2. Use json_serializable

Open in the IDE `Setting` -> `Tools` -> `DartMappable Settings`,  selected `json_serializable`.  
Don't forget to set up `json_serializable` into your project : https://pub.dev/packages/json_serializable  
Add into your `pubspec.yaml`
```yaml
dependencies:
  json_annotation: ^4.8.0
dev_dependencies:
  build_runner: ^2.3.3
  json_serializable: ^6.6.0
```
Or Run Terminal
```shell
flutter pub add json_annotation
flutter pub add build_runner --dev
flutter pub add json_serializable --dev
```
<!-- Plugin description end -->