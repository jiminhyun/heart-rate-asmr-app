package com.example.moveair5.relatedmusic;

import com.example.moveair5.R;

public class Song {
    public static int[][] resid ={{R.raw.baldych_lunar_path, R.raw.musa, R.raw.new_wave2012, R.raw.sphene, R.raw.step_into_the_light,
            R.raw.the_song_of_the_blue_dolphin, R.raw.campfire, R.raw.cuckoo, R.raw.rainthunder, R.raw.cave, R.raw.rain2, R.raw.benitoite,
            R.raw.carrain, R.raw.cicadacry, R.raw.blissful_sky, R.raw.bruwynn, R.raw.cassiterite, R.raw.diamond, R.raw.empty_reef,
            R.raw.herbstsommer, R.raw.meditation_forest},

            {R.raw.kein_lebewohl, R.raw.snowing, R.raw.variatio12, R.raw.variatio16, R.raw.winter_is_coming, R.raw.road, R.raw.ballpen,
                    R.raw.rain_focus, R.raw.rainroad, R.raw.nature_focus, R.raw.clock, R.raw.after_being_indestructible, R.raw.antonio_vivaldi_four_seasons,
                    R.raw.contrapunctus, R.raw.dark_clouds, R.raw.debussy, R.raw.drive_me_gently, R.raw.ludwig_thuille, R.raw.quiescent},

            {R.raw.natural_water, R.raw.partita_no3, R.raw.partita_no5, R.raw.the_poet_speaks, R.raw.silence, R.raw.waterfront,
                    R.raw.birdsong_by_the_river, R.raw.card, R.raw.christmas, R.raw.summercicada, R.raw.blacksmith, R.raw.alessandro, R.raw.japaninstruments,
                    R.raw.umbrella, R.raw.birdsong_by_the_river2, R.raw.snowsound, R.raw.woodclock, R.raw.clocktower, R.raw.nature_meditate, R.raw.clockecho,
                    R.raw.forest, R.raw.windchime, R.raw.aria, R.raw.waltzes, R.raw.canonisches, R.raw.lento_con_gran, R.raw.deux_arabesques,
                    R.raw.landliches_lied, R.raw.op_47_imov},

            {R.raw.alone, R.raw.cyberpunk, R.raw.right_now, R.raw.fashion_city, R.raw.gladiolus_rag, R.raw.movement_of_life, R.raw.passion,
                    R.raw.move_in_on_dub, R.raw.move_in_on, R.raw.isonation, R.raw.tread_lightly, R.raw.foliorum, R.raw.manga, R.raw.meant_to_be,
                    R.raw.illusion, R.raw.sport_electro, R.raw.training_day, R.raw.river_in_the_sky,R.raw.virus},

            {R.raw.josefpres_piano018, R.raw.josefpres_piano014, R.raw.a_strangers_love, R.raw.autumn_stroll, R.raw.awake_at_midnight, R.raw.decision,
                    R.raw.every_thought_of_you, R.raw.nightmare_shadow, R.raw.no_rise_no_fall, R.raw.sensory_garden, R.raw.springtime_eve, R.raw.sudden_memory,
                    R.raw.beach, R.raw.morning_bird, R.raw.frog_stream, R.raw.windchime_relax, R.raw.sound_of_a_ship}};
    //행은 장르, 열은 노래로 참조 즐찾에서는 data+행*100+열+100으로 db 설정함
    // int a=(x-100)/100 => 행)(genre)
    // int b=(x-100)%100 => 열(route)
}
