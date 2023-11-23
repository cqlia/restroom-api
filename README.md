# Restroom App API

Backend API for a class project, which utilized an app to manage a list of restrooms.

Given that this code was not submitted, there shouldn't be any issues with making it public.
What _was_ submitted was the app it was attached to.
The implementation of this frontend is up to the reader.

An OpenAPI spec is available [here](docs/api.yaml).

## Usage

> [!WARNING]
> There is no moderation functionality in the API.  
> Please do not run this as a public application!!

1. Install PostgreSQL with citext and PostGIS extensions.
2. Load the [definitions SQL file](src/main/resources/base.sql) from the `resources` directory.
3. Modify the [reference.conf](src/main/resources/reference.conf) for your setup and rename it to `application.conf`.
    - Alternatively, a different `application.conf` can be loaded by setting the `config.file` system property.
4. Assemble the application in the sbt shell by running `assembly`. 
5. Deploy the assembled application.

## License

This code is licensed under the [zero-clause BSD license](LICENSE.txt).
Using this backend for a school project is **not** advised if you happen to have the same professor as me.

Structure heavily based off the [zio-scala3-quickstart](https://github.com/ScalaConsultants/zio-scala3-quickstart.g8/tree/master).

This code is unmaintained and should be used as a reference at best.
