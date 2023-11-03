# DartMappable(Flutter Json To Dart Converter)

## How to use

1. GitHub [Releases](https://github.com/eitanliu/dart_mappable_plugin/releases) or IntelliJ IDEs Plugin Marketplace [DartMappable](https://plugins.jetbrains.com/plugin/21845) page download plugin
2. Open in the IDE `Setting` -> `Plugins`, right click setting icon ⚙️ -> `Install Plugin from Disk...` selected you download file.

<!-- Plugin description -->
Provide JSON to dart data classes conversion for use in Flutter and Dart projects.  
Support multiple serialization schemes with [json_serializable](https://pub.dev/packages/json_serializable), [dart_mappable](https://pub.dev/packages/dart_mappable), [freezed](https://pub.dev/packages/freezed).  

Right click on package -> `New` -> `Json To DartMappable`　And Then you will know how to use.  
After converting JSON to dart classes, will be automatically run 'flutter pub run build_runner build --delete-conflicting-outputs'.  

If you haven't added the dependency, it will automatically execute the command to add it.  

If you change the fields in the class, right click on package -> `Flutter Command` -> `Flutter Run Build Runner`, shortcut key is `Alt + r`.  

### 1. Use DartMappable

Open in the IDE `Setting` -> `Tools` -> `DartMappable Settings`,  selected `dart_mappable`.  
Don't forget to set up `dart_mappable` into your project : https://pub.dev/packages/dart_mappable  
Add into your `pubspec.yaml`  
```yaml
dependencies:
  dart_mappable: ^3.0.0  
dev_dependencies: 
  build_runner: ^2.1.0
  dart_mappable_builder: ^3.0.2
```
Or Run command in Terminal  
```shell
flutter pub add dart_mappable
flutter pub add build_runner --dev
flutter pub add dart_mappable_builder --dev
```

### 2. Use JsonSerializable

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
Or Run command in Terminal
```shell
flutter pub add json_annotation
flutter pub add build_runner --dev
flutter pub add json_serializable --dev
```

### 3. Use Freezed

Open in the IDE `Setting` -> `Tools` -> `DartMappable Settings`,  selected `freezed`.  
Don't forget to set up `freezed` into your project : https://pub.dev/packages/freezed  
Add into your `pubspec.yaml`
```yaml
dependencies:
  freezed_annotation: ^2.4.1
dev_dependencies: 
  build_runner: ^2.1.0
  freezed: ^2.4.1
```
Or Run command in Terminal
```shell
flutter pub add freezed_annotation
flutter pub add --dev build_runner
flutter pub add --dev freezed
# if using freezed to generate fromJson/toJson, also add:
flutter pub add json_annotation
flutter pub add --dev json_serializable
```
<!-- Plugin description end -->