<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

class CreatePartChallengesTable extends Migration
{
    /**
     * Run the migrations.
     *
     * @return void
     */
    public function up()
    {
        Schema::create('part_challenges', function (Blueprint $table) {
            $table->unsignedBigInteger('challengeNo');
            $table->unsignedBigInteger('participantID');

            $table->primary(['challengeNo', 'participantID']); // Composite primary key

            // Foreign keys
            $table->foreign('challengeNo')
                  ->references('challengeNo')
                  ->on('challenges')
                  ->onDelete('cascade');
            
            $table->foreign('participantID')
                  ->references('participantID')
                  ->on('participants')
                  ->onDelete('cascade');

            $table->timestamps(); // Adds created_at and updated_at columns
        });
    }

    /**
     * Reverse the migrations.
     *
     * @return void
     */
    public function down()
    {
        Schema::dropIfExists('part_challenges');
    }
}
