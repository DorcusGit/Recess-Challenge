<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

class CreateAnswersTable extends Migration
{
    /**
     * Run the migrations.
     *
     * @return void
     */
    public function up()
    {
        Schema::create('answers', function (Blueprint $table) {
            $table->id('answerNo'); // Auto-incrementing primary key
            $table->text('answer');
            $table->integer('marksAwarded');
            
            // Ensure these columns match the data types in referenced tables
            $table->unsignedBigInteger('answerBankID'); // Foreign key
            $table->foreign('answerBankID')
                  ->references('answerBankID')
                  ->on('answer_banks') // Correct table name
                  ->onDelete('cascade');
            
            $table->unsignedBigInteger('questionNo'); // Foreign key
            $table->foreign('questionNo')
                  ->references('questionNo')
                  ->on('questions') // Correct table name
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
        Schema::dropIfExists('answers');
    }
}
