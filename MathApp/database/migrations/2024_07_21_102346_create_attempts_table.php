<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

class CreateAttemptsTable extends Migration
{
    /**
     * Run the migrations.
     *
     * @return void
     */
    public function up()
    {
        Schema::create('attempts', function (Blueprint $table) {
            $table->id('attemptID'); // Auto-incrementing primary key
            $table->time('startTime');
            $table->time('endTime');
            $table->integer('score');
            $table->decimal('percentageMark', 5, 2);

            // Foreign keys
            $table->unsignedBigInteger('participantID');
            $table->foreign('participantID')
                  ->references('participantID')
                  ->on('participants') // Correct table name
                  ->onDelete('cascade');
            
            $table->unsignedBigInteger('challengeNo');
            $table->foreign('challengeNo')
                  ->references('challengeNo')
                  ->on('challenges') // Correct table name
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
        Schema::dropIfExists('attempts');
    }
}
