<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

class CreateAttemptqnsTable extends Migration
{
    /**
     * Run the migrations.
     *
     * @return void
     */
    public function up()
    {
        Schema::create('attemptqns', function (Blueprint $table) {
            $table->unsignedBigInteger('attemptID');
            $table->unsignedBigInteger('questionNo');
            $table->integer('score')->nullable(); // Nullable in case no score is provided
            $table->text('givenAnswer')->nullable(); // Nullable in case no answer is given

            $table->primary(['attemptID', 'questionNo']); // Composite primary key

            // Foreign keys
            $table->foreign('attemptID')
                  ->references('attemptID')
                  ->on('attempts')
                  ->onDelete('cascade');
            
            $table->foreign('questionNo')
                  ->references('questionNo')
                  ->on('questions')
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
        Schema::dropIfExists('attemptqns');
    }
}
