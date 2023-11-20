CREATE EXTENSION IF NOT EXISTS postgis;

CREATE TABLE IF NOT EXISTS restrooms (
    id uuid PRIMARY KEY NOT NULL DEFAULT gen_random_uuid(),
    title text NOT NULL,
    location geometry(POINT, 4326) NOT NULL,
    description text
);

CREATE TABLE IF NOT EXISTS reviews (
    id uuid PRIMARY KEY NOT NULL DEFAULT gen_random_uuid(),
    restroom_id uuid NOT NULL,
    rating float NOT NULL,
    body text,
    created_at timestamptz NOT NULL DEFAULT now(),
    created_by text NOT NULL,

    CONSTRAINT fk_restroom FOREIGN KEY(restroom_id) REFERENCES restrooms(id)
);
